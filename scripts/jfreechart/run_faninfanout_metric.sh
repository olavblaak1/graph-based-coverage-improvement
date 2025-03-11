mainClass="com.kuleuven.GraphAnalyzer.Main"
graphPath="data/jfreechart/analysis/coverageGraph.json"
metric="FAN_IN_AND_FAN_OUT"
outputPath="data/jfreechart/metrics/$metric.json"


mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="jfreechart $metric"