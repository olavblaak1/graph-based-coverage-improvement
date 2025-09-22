# Exponentially-Weighted Test Case Prioritization Based on an Enhanced Graph Representation of Software



## Automation Scripts

This section explains the scripts used to automate mutation testing, metric analysis, test  prioritization, and APFD calculation for a system.

### APFD Pipeline Script

The `calculateAPFDMetrics.sh` script automates the full pipeline for one or more systems. It performs mutation testing, computes software metrics, prioritizes the test suite, and calculates APFD scores. 

#### Usage

```
sh scripts/calculateAPFDMetrics.sh <systemName1> [<systemName2> ...]
```

Each `<systemName>` must exist under `systems/<systemName>`, be a Maven project. It must also have the usual Java project structure, with `<systemName>/src/main` considered the input system's source directory, and `<systemName>/src/test` the directory containing all test suites.

#### Workflow

For each system provided, the script performs the following steps:

1. **Mutation Testing (Pitest)**
    - Runs Pitest with the full mutation matrix enabled.
    - Stores reports under:
      ```
      data/<systemName>/metrics/pitest-report-full/
      ```

2. **Graph & Metric Analysis**
    - Extracts dependencies (`scripts/getDependencies.sh`).
    - Builds the full graph (`scripts/analyzeGraphFull.sh`).
    - Computes graph-based metrics (e.g., `FAN_IN_AND_FAN_OUT`, `PAGERANK`, `HITS`) with:
      ```
      scripts/runMetricAnalysis.sh
      ```

3. **Test Prioritization**
    - Runs `scripts/runTestMinimization.sh` with different strategies:
        - `EXP_SCORE_BASED`
        - `SCORE_BASED`
        - `COMBINED`
    - Copies `minimizedTests.json` to the corresponding results folder.

4. **APFD Calculation**
    - Runs the Python script `src/main/python/analyzeAPFD.py` to compute APFD scores.
    - Extracts APFD results and writes them to:
      ```
      data/<systemName>/metrics/<strategy>/apfds.txt
      ```


#### Outputs

- For each strategy and metric combination:
  ```
  data/<systemName>/metrics/<strategy>/<metric>/apfds.txt
  ```
- Pitest mutation reports:
  ```
  data/<systemName>/metrics/pitest-report-full/
  ```

#### Notes

- The script relies on supporting scripts located in the `scripts/` folder:
    - `getDependencies.sh`
    - `analyzeGraphFull.sh`
    - `runMetricAnalysis.sh`
    - `runTest prioritization.sh`  
- While some files often refer to test "minimization", the approach still prioritizes tests and does not minimize the test suite, this is an artifact from initial research, which was focused on test minimization


### Boxplot Graphing Script

The `src/main/python/CreateGraphUpdated.py` script generates boxplot visualizations and summary statistics for APFD values after running the full pipeline.

#### Usage

 ```
python3 CreateGraphUpdated.py
 ```

#### Requirements

- Python 3
- Dependencies: `numpy`, `matplotlib`, `scipy`, `pandas`

Install dependencies if needed:

 ```
pip install numpy matplotlib scipy pandas
 ```

#### Inputs

- The script expects APFD result files at:
    -  ```data/<systemName>/metrics/<strategy>/<metric>/apfds.txt```
    -  ```data/<systemName>/random_apfds.txt```

The systems, metrics, and strategies to include are configured inside the script.

#### Outputs

- Boxplot figures (`.eps`) for each system, saved in the working directory:
    -  ```
       <systemName>_boxplot.eps
       ```
- A CSV file with effect sizes and p-values:
    -  ```
       apfd_stats.csv
       ```
- A LaTeX table summarizing the results:
    -  ```
       apfd_stats.tex
       ```