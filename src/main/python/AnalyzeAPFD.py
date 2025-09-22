import json
import random
import re
import sys
import urllib.parse
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path


def normalize_test_name(raw_name: str) -> str:
    """
    Normalize a test name to the format `class.method()`.

    Context:
    Pitest uses multiple formats to describe test methods:
    - Standard JUnit format: `class.method(class)`
    - Pitest `killingTest` format, e.g. `.../[class:...]...[method:...]`
    - JVM array notation for parameters (`[L...;`)

    This function unifies these into a standard Java signature-like format, which
    ensures consistency across different Pitest outputs.

    Special handling:
    - Converts `$` (inner class notation in JVM) to `.`
    - Converts JVM array descriptors to `[]` in Java notation
    - Falls back to the raw name if the format cannot be parsed
    """
    try:
        raw_name = urllib.parse.unquote(raw_name)

        # Format: class.method(class) -> class.method()
        match = re.match(r"([\w.$]+)\([\w.$]+\)", raw_name)
        if match:
            return match.group(1).replace('$', '.') + "()"

        # Format: killingTest string from Pitest
        if '/[class:' in raw_name:
            class_part = raw_name.split('/[class:')[1].split(']')[0].replace('/', '.').replace('$', '.')

            if '/[method:' in raw_name:
                method = raw_name.split('/[method:')[1].split(')')[0] + ')'
            elif '/[test-template:' in raw_name:
                method = raw_name.split('/[test-template:')[1].split(')')[0] + ')'
            else:
                return raw_name  # fallback if neither method nor test-template exists

            # Convert JVM array notation to Java syntax
            method = re.sub(
                r'\[L([^;]+);',
                lambda m: m.group(1).replace('/', '.').removeprefix('L') + '[]',
                method,
            )
            method = method.replace('$', '.')

            return f"{class_part}.{method}"

        return raw_name

    except Exception as e:
        print(f"Normalization error for: {raw_name} → {e}")
        return raw_name


def parse_ranked_tests(json_path: Path) -> dict:
    """
    Read the minimizedTests.json file produced by the ranking strategy.

    Output:
    A mapping of test names → ranking value from the input file.
    These are not final execution orders yet, just raw strategy values.
    """
    with open(json_path, "r") as f:
        data = json.load(f)
    return {
        entry["name"]: entry["rank"] for entry in data["minimizedTests"]
    }


def parse_killed_mutations(xml_path: Path) -> dict:
    """
    Parse Pitest's full mutations.xml to map killed mutations → tests that killed them.

    Details:
    - Only considers mutations with `status="KILLED"`.
    - Normalizes test names with `normalize_test_name`.
    - Returns a mapping from mutation XML nodes to lists of test names.

    The XML node itself is kept as the key because later computations
    only need mutation identity, not just a string label.
    """
    tree = ET.parse(xml_path)
    root = tree.getroot()
    mutations_to_tests = defaultdict(list)

    for mutation in root.findall("mutation"):
        if mutation.attrib.get("status") != "KILLED":
            continue

        killing_tests = mutation.find("killingTests").text or ""
        if killing_tests.split("|") == "":
            raise Exception(
                "Pitest reported a killed mutation without killingTests. "
                "Rerun Pitest with dFullMutationMatrix=true"
            )

        mutations_to_tests[mutation] = [normalize_test_name(test) for test in killing_tests.split("|")]

    print("TOTAL INITIAL MUTATIONS: " + str(len(root.findall("mutation"))))
    print("TOTAL INITIAL KILLED MUTATIONS: " + str(len([mutation for mutation in root.findall("mutation") if mutation.attrib.get("status") == "KILLED"])))

    return mutations_to_tests


def calculate_apfd(mutations_to_tests: dict, ranked_tests: dict) -> float:
    """
    Compute the APFD (Average Percentage of Fault Detection).

    Formula:
    APFD = 1 - (Σ ranks of first killing tests / (numMutations * numTests)) + (1 / (2 * numTests))

    Inputs:
    - mutations_to_tests: mutation → [tests killing it]
    - ranked_tests: test → rank (execution position)

    Notes:
    - Uses the earliest ranked test that kills each mutation.
    - Assumes ranked_tests contains all tests in consideration.
    """
    total_killed = len(mutations_to_tests)
    if total_killed == 0:
        return 0.0

    test_count = len(ranked_tests)
    sum_ranks = sum(
        min(ranked_tests[t] for t in tests)
        for tests in mutations_to_tests.values()
    )
    apfd = 1 - (sum_ranks / (total_killed * test_count)) + (1 / (2 * test_count))
    return apfd


def get_best_test(remaining_mutations, mutations_to_tests, remaining_tests):
    """
    Greedy heuristic for optimal ordering:
    Select the test that currently kills the most remaining mutations.
    """
    mutation_killing_count = defaultdict(int)
    for mutation in remaining_mutations:
        for test in mutations_to_tests[mutation]:
            if test not in remaining_tests:
                continue
            mutation_killing_count[test] += 1

    return max(mutation_killing_count, key=lambda x: mutation_killing_count[x]) if mutation_killing_count else None


def get_worst_test(remaining_mutations, mutations_to_tests, remaining_tests):
    """
    Greedy heuristic for worst ordering:
    Select the test that currently kills the fewest remaining mutations.
    If multiple tie, a random one is picked.
    """
    mutation_killing_count = defaultdict(int)
    for mutation in remaining_mutations:
        for test in mutations_to_tests[mutation]:
            if test not in remaining_tests:
                continue
            mutation_killing_count[test] += 1

    if len(mutation_killing_count) == 0:
        return None

    min_kills = min(mutation_killing_count.values())
    worst_tests = [test for test, count in mutation_killing_count.items() if count == min_kills]
    return random.choice(worst_tests)


def calculate_worst_order(mutations_to_tests: dict, ranked_tests: dict) -> dict:
    """
    Compute an intentionally poor ordering: mutations are killed as late as possible.

    Greedy process:
    - Iteratively pick the test that kills the least remaining mutations.
    - After exhausting useful tests, append those that kill nothing.
    """
    remaining_mutations = set(mutations_to_tests.keys())
    remaining_tests = set(ranked_tests.keys())
    worst_order = []

    while remaining_mutations:
        worst_test = get_worst_test(remaining_mutations, mutations_to_tests, remaining_tests)
        if worst_test is None:
            break
        worst_order.append(worst_test)
        remaining_mutations -= set(
            [mutation for mutation in mutations_to_tests if worst_test in mutations_to_tests[mutation]]
        )
        remaining_tests.remove(worst_test)

    worst_order += sorted(remaining_tests)
    new_test_ranks = {test: rank for rank, test in enumerate(worst_order, start=1)}

    assert set(new_test_ranks.keys()) == set(ranked_tests), "Mismatch in test sets"
    return new_test_ranks


def calculate_worst_apfd(mutations_to_tests: dict, ranked_tests: dict) -> float:
    """APFD for the computed worst order."""
    return calculate_apfd(mutations_to_tests, calculate_worst_order(mutations_to_tests, ranked_tests))


def calculate_optimal_order(mutations_to_tests: dict, ranked_tests: dict) -> dict:
    """
    Compute an order that kills mutations as early as possible.

    Greedy process:
    - Iteratively pick the test that kills the most remaining mutations.
    - After exhausting useful tests, append those that kill nothing.
    """
    remaining_mutations = set(mutations_to_tests.keys())
    remaining_tests = set(ranked_tests.keys())
    optimal_order = []

    while remaining_mutations:
        best_test = get_best_test(remaining_mutations, mutations_to_tests, remaining_tests)
        if best_test is None:
            break
        optimal_order.append(best_test)
        remaining_mutations -= set(
            [mutation for mutation in mutations_to_tests if best_test in mutations_to_tests[mutation]]
        )
        remaining_tests.remove(best_test)

    optimal_order += sorted(remaining_tests)
    optimal_tests = {test: rank for rank, test in enumerate(optimal_order, start=1)}

    assert set(optimal_tests.keys()) == set(ranked_tests), "Mismatch in test sets"
    return optimal_tests


def calculate_optimal_apfd(mutations_to_tests: dict, ranked_tests: dict) -> float:
    """APFD for the computed optimal order."""
    return calculate_apfd(mutations_to_tests, calculate_optimal_order(mutations_to_tests, ranked_tests))


def calculate_random_apfd(mutations_to_tests: dict, ranked_tests: dict) -> float:
    """
    APFD for a random permutation of the test suite.

    Useful as a baseline to compare against strategy-based orderings.
    """
    random_order = list(ranked_tests.keys())
    random.shuffle(random_order)
    random_ranks = {test: rank for rank, test in enumerate(random_order, start=1)}
    return calculate_apfd(mutations_to_tests, random_ranks)


def filter_tests(mutations_to_tests: dict, ranked_tests: dict) -> tuple:
    """
    Remove irrelevant tests and mutations before APFD calculation.

    Steps:
    - Drop tests not in ranked_tests.
    - Drop mutations that end up with no killing test left.
    - Drop tests that do not kill any mutation after filtering.
    - Reassign contiguous ranks (1..n) to the remaining tests.

    Logs statistics about removals and warns if over 10% are dropped.
    """
    filtered_mutations_to_tests = {
        mutation: [test for test in tests if test in ranked_tests]
        for mutation, tests in mutations_to_tests.items()
    }
    filtered_mutations_to_tests = {
        mutation: tests for mutation, tests in filtered_mutations_to_tests.items() if tests
    }
    used_tests = set(test for tests in filtered_mutations_to_tests.values() for test in tests)
    sorted_tests = sorted(used_tests, key=lambda t: ranked_tests[t])
    filtered_ranked_tests = {test: rank + 1 for rank, test in enumerate(sorted_tests)}

    total_mutations = len(mutations_to_tests)
    total_tests = len(ranked_tests)
    removed_mutations = total_mutations - len(filtered_mutations_to_tests)
    removed_tests = total_tests - len(filtered_ranked_tests)

    print(f"Removed {removed_mutations} useless mutations.")
    print(f"Removed {removed_tests} unused tests (not ranked or not killing).")

    if removed_mutations / max(1, total_mutations) > 0.1:
        print(" Warning: More than 10% of mutations removed.")
    if removed_tests / max(1, total_tests) > 0.1:
        print("️ Warning: More than 10% of tests removed.")

    return filtered_mutations_to_tests, filtered_ranked_tests


def get_partitioned_tests(parsed_tests):
    """
    Partition tests by their raw strategy value.

    Output:
    value → list of tests that share this value.
    Used for randomized tie-breaking within strategy-equivalent groups.
    """
    partitions = {}
    for test in parsed_tests.keys():
        try:
            partitions[parsed_tests[test]].append(test)
        except KeyError:
            partitions[parsed_tests[test]] = [test]
    return partitions


def calculate_randomized_apfds_per_partition(partitioned_tests, filtered_mutations_to_tests, filtered_tests):
    """
    For tests with identical strategy values, shuffle their internal order
    repeatedly and compute APFDs.

    Produces a distribution of APFD values (500 samples by default),
    which captures the sensitivity of results to arbitrary tie-breaking.
    """
    apfds = []
    for i in range(500):
        ordered_ranked_tests = {}
        j = 1
        for value in sorted(partitioned_tests.keys(), reverse=True):
            random_order_tests = partitioned_tests[value]
            random.shuffle(random_order_tests)
            for test in random_order_tests:
                if test in filtered_tests.keys():
                    ordered_ranked_tests[test] = j
                    j += 1
        apfd = calculate_apfd(filtered_mutations_to_tests, ordered_ranked_tests)
        apfds.append(apfd)
    return apfds


def main(system_name: str, strategy: str, metric: str):
    """
    Entry point for analyzing APFD values of a test prioritization strategy.

    Workflow:
    1. Load test rankings (`minimizedTests.json`).
    2. Parse Pitest report (`mutations.xml`) for killed mutations.
    3. Construct ranked tests and partition by strategy values.
    4. Filter out irrelevant tests/mutations.
    5. Compute APFD values for randomized tie-breakings.
    6. Save APFDs to `apfds.txt` under the metric's output folder.
    """
    base_path = Path(f"./data/{system_name}")
    json_path = base_path / "metrics" / strategy / metric / "minimizedTests.json"
    xml_path = base_path / "metrics/pitest-report-full/mutations.xml"

    test_values = parse_ranked_tests(json_path)
    mutations_to_tests = parse_killed_mutations(xml_path)

    test_ranks = {
        name: rank
        for rank, name in enumerate(
            sorted(test_values.keys(), key=lambda x: test_values[x], reverse=True),
            start=1,
        )
    }
    partitioned_tests = get_partitioned_tests(test_values)

    print("Filtering ignored tests...")
    filtered_mutations_to_tests, filtered_ranked_tests = filter_tests(mutations_to_tests, test_ranks)

    print("Number of tests:", len(filtered_ranked_tests))
    print("Number of killed mutations:", len(filtered_mutations_to_tests))

    print("\nWriting APFD values...")
    apfds = calculate_randomized_apfds_per_partition(partitioned_tests, filtered_mutations_to_tests, filtered_ranked_tests)

    with open(base_path / "metrics" / strategy / metric / "apfds.txt", "w") as f:
        for apfd in apfds:
            f.write(f"{apfd}\n")
    return


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python AnalyzeAPFD.py <system_name> <strategy> <metric>")
        sys.exit(1)

    system_name = sys.argv[1]
    strategy = sys.argv[2]
    metric = sys.argv[3]
    main(system_name, strategy, metric)