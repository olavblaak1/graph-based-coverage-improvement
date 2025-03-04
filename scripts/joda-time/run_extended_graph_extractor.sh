systemName="joda-time"

# GRAPH EXTRACTION
mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
classPaths="systems/$systemName/target/classpath.txt"

extractionMethod="FULL_GRAPH"

mvn dependency:build-classpath -Dmdep.outputFile=$classPaths
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $extractionMethod"
