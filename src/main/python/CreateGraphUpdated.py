import csv
import os
import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import mannwhitneyu


# --- Settings ---
base_dir = "./data"
systems = ["gson/gson", "commons-csv", "jfreechart"]
strategies = ["SCORE_BASED", "EXP_SCORE_BASED", "COMBINED"]  # desired order
metrics = ["DIT", "NOOM", "FIELD_ACCESS", "PAGERANK", "HITS", "BETWEENNESS"] #, "FAN_IN_AND_FAN_OUT"]
metric_names = ["DIT", "NOOM", "Field Access", "PageRank", "HITS", "Betweenness"]# , "Fan in and fan out"]

strategy_colors = {
    "SCORE_BASED": "#4E79A7",      # muted blue
    "EXP_SCORE_BASED": "#E15759",  # muted red
    "COMBINED": "#76B7B2"          # teal
}
strategy_legend_names = ["Score-Based", "Exponential Score-Based", "Combined"]

# for randomized:
randomized_color = "#59A14F"  # muted green

def cliffs_delta(x, y):
    """
    Compute Cliff's delta effect size between two samples x and y.
    Returns delta in [-1, 1].
    """
    x = np.array(x)
    y = np.array(y)
    n_x, n_y = len(x), len(y)
    greater = sum(xi > yj for xi in x for yj in y)
    smaller = sum(xi < yj for xi in x for yj in y)
    delta = (greater - smaller) / (n_x * n_y)
    return delta

# --- Helper ---
def compute_box_stats(values):
    q1 = np.percentile(values, 25)
    med = np.percentile(values, 50)
    q3 = np.percentile(values, 75)
    iqr = q3 - q1
    whislo = np.min(values[values >= q1 - 1.5*iqr])
    whishi = np.max(values[values <= q3 + 1.5*iqr])
    fliers = [v for v in values if v < whislo or v > whishi]
    return {"q1": q1, "med": med, "q3": q3, "whislo": whislo, "whishi": whishi, "fliers": fliers}


results = []

# --- Plotting ---
for system in systems:
    fig, ax = plt.subplots(figsize=(16, 6))
    box_data = []
    positions = []
    colors = []

    n_metrics = len(metrics)
    n_strategies = len(strategies)
    cluster_gap = 0.5
    box_width = 0.6
    cluster_centers = []

    random_file = os.path.join(base_dir, system, "random_apfds.txt")
    randomized_values = []
    if os.path.exists(random_file):
        randomized_values = np.array([float(x.strip()) for x in open(random_file) if x.strip()])


    # --- Plot each metric cluster ---
    for i, metric in enumerate(metrics):
        cluster_start = (cluster_gap / 2) if i == 0 else i * (n_strategies + cluster_gap)
        cluster_box_positions = []

        for j, strategy in enumerate(strategies):
            pos = cluster_start + j
            cluster_box_positions.append(pos)
            file_path = os.path.join(base_dir, system, "metrics", strategy, metric, "apfds.txt")
            if not os.path.exists(file_path):
                print(f"File {file_path} does not exist")
                continue
            values = np.array([float(x.strip()) for x in open(file_path) if x.strip()])
            stats = compute_box_stats(values)
            box_data.append(stats)
            positions.append(pos)
            colors.append(strategy_colors[strategy])


            # Compute effect size
            delta = cliffs_delta(values, randomized_values)

            # Compute p-value
            stat, p_value = mannwhitneyu(values, randomized_values, alternative='two-sided')


            # Compute mean
            mean_value = np.mean(values)
            randomized_mean = np.mean(randomized_values) if len(randomized_values) > 0 else np.nan

            # Save results
            results.append({
                "System": system,
                "Metric": metric,
                "Strategy": strategy,
                "Mean": mean_value,
                "Randomized Mean": randomized_mean,
                "Cliffs Delta": delta,
                "P-value": p_value
            })
        # Compute center for the x-tick label
        cluster_centers.append(np.mean(cluster_box_positions))

        # Draw vertical line separating clusters
        if i == 0:
            ax.axvline(x=cluster_box_positions[-1] + box_width, color='gray', linestyle='-', linewidth=0.5)
        elif i < len(metrics) - 1:
            ax.axvline(x=cluster_box_positions[-1] + cluster_gap + box_width/2, color='gray', linestyle='-', linewidth=0.5)

    # --- Add randomized box at the end ---
    random_file = os.path.join(base_dir, system, "random_apfds.txt")
    if os.path.exists(random_file):
        values = np.array([float(x.strip()) for x in open(random_file) if x.strip()])
        stats = compute_box_stats(values)
        box_data.append(stats)
        # Place randomized box just after last cluster
        pos = cluster_box_positions[-1] + 2*cluster_gap + box_width
        positions.append(pos)
        colors.append(randomized_color)  # nicer green for Randomized
        cluster_centers.append(pos)  # include in x-ticks

        # Draw a "double-line" separator before randomized
        sep_x = pos - box_width - cluster_gap
        ax.axvline(x=sep_x - 0.03, color='gray', linestyle='-', linewidth=1)
        ax.axvline(x=sep_x + 0.03, color='gray', linestyle='-', linewidth=1)

        # Add extra whitespace after randomized box so label fits
        ax.set_xlim(left=-0.5, right=pos + box_width + cluster_gap)

    # --- Draw boxplots ---
    bxp = ax.bxp(box_data, positions=positions, widths=box_width, patch_artist=True, showfliers=True,
                 medianprops=dict(color='black', linewidth=2))

    # Apply uniform color to every element of each boxplot
    for patch, color, flier in zip(bxp['boxes'], colors, bxp['fliers']):
        patch.set_facecolor(color)
        patch.set_edgecolor("black")
    for whisker, color in zip(bxp['whiskers'], np.repeat(colors, 2)):
        whisker.set(color=color, linewidth=1.5)
    for cap, color in zip(bxp['caps'], np.repeat(colors, 2)):
        cap.set(color=color, linewidth=1.5)
    for flier, color in zip(bxp['fliers'], colors):
        flier.set(markerfacecolor=color, marker='o', alpha=0.6, markeredgecolor="black")

    # --- X-ticks ---
    ax.set_xticks(cluster_centers)
    ax.set_xticklabels(metric_names + ["Randomized\n(baseline)"], rotation=0, ha='center', fontsize=14)

    ax.set_ylabel("APFD", fontsize=14)
    ax.set_ylim(0.60, 0.95)


    if len(randomized_values) > 0:
        ax.axhline(y=randomized_mean, color="gray", linestyle="--", linewidth=1.2, label="Randomized Mean")


    # --- Legend ---
    handles = [plt.Line2D([0], [0], color=c, lw=4) for c in strategy_colors.values()]
    handles.append(plt.Line2D([0], [0], color="gray", lw=1.2, linestyle="--"))  # legend entry
    labels = strategy_legend_names + ["Randomized Mean"]

    ax.legend(handles, labels, fontsize=15)

    plt.tight_layout()
    plt.savefig(f"{system.replace('/', '_')}_boxplot.eps", format="eps", dpi=300)
    plt.show()


# --- Save results as CSV ---
csv_file = "apfd_stats.csv"
with open(csv_file, "w", newline="") as f:
    writer = csv.DictWriter(f, fieldnames=["System", "Metric", "Strategy", "Mean", "Randomized Mean", "Cliffs Delta", "P-value"])
    writer.writeheader()
    writer.writerows(results)


import pandas as pd

df = pd.read_csv("apfd_stats.csv")

with open("apfd_stats.tex", "w") as f:
    f.write("\\begin{tabular}{l l l c c c c}\n")
    f.write("\\hline\n")
    f.write("System & Metric & Strategy & Mean & Randomized Mean & Cliff's Delta & P-value \\\\\n")
    f.write("\\hline\n")
    for _, row in df.iterrows():
        pval = row['P-value']
        pval_str = "<0.001" if pval < 0.001 else f"{pval:.3f}"
        f.write(f"{row['System']} & {row['Metric']} & {row['Strategy']} & {row['Mean']:.3f} & {row['Randomized Mean']:.3f} & {row['Cliffs Delta']:.3f} & {pval_str} \\\\\n")
    f.write("\\hline\n")
    f.write("\\end{tabular}\n")