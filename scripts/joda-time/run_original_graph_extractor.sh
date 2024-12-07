mainClass="com.kuleuven.Main"
graphPath="data/joda-time/graph/graph.json"
srcDir="systems/joda-time/src"
jarPath="systems/joda-time/target/joda-time-2.13.0.jar"

extractionMethod="ORIGINAL"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $srcDir $jarPath $extractionMethod"