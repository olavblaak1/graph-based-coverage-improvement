mainClass="com.kuleuven.TestMinimization.Main"
testInput="systems/joda-time/src/test/java/org/joda/time"
jarPath="systems/joda-time/target/classpath.txt"
srcDir="systems/joda-time/src/main/java"
graphPath="data/joda-time/metrics/FAN_IN_AND_FAN_OUT.json"

analysisStrategy="STANDARD"

mvn dependency:build-classpath -Dmdep.outputFile=$jarPath
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $testInput $jarPath $srcDir $analysisStrategy"