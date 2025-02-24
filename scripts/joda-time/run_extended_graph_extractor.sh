mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
graphPath="data/joda-time/graph/graph.json"
srcDir="systems/joda-time/src/main/java"
classPaths="target/classpath.txt"

extractionMethod="FULL_GRAPH"
metric="FAN_IN_AND_FAN_OUT"

mvn dependency:build-classpath -Dmdep.outputFile=$classPaths
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $srcDir $classPaths $extractionMethod $metric"