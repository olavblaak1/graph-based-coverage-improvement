mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
graphPath="data/joda-time/graph/graph.json"
srcDir="systems/joda-time/src/main/java"
jarPath="systems/joda-time/target/joda-time-2.13.0.jar"

extractionMethod="FULL_GRAPH"
metric="FAN_IN_AND_FAN_OUT"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $srcDir $jarPath $extractionMethod $metric"