mainClass="com.kuleuven.GraphAnalyzer.Main"
graphPath="data/joda-time/analysis/coverageGraph.json"
metric="NOOM"
outputPath="data/joda-time/metrics/noom.json"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $metric $outputPath"