mainClass="com.kuleuven.GraphAnalyzer.Main"
graphPath="data/joda-time/analysis/coverageGraph.json"
metric="FAN_IN_AND_FAN_OUT"
outputPath="data/joda-time/metrics/$metric.json"


mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $metric $outputPath"