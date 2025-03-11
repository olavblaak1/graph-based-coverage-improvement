systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

# Check if data/ directory exists
if [ ! -d "data" ]; then
  echo "Error: data/ directory does not exist."
  exit 1
fi

classPaths="target/classpath.txt"


currentDir=$(pwd)
cd systems/$systemName || exit

OUTPUT_FILE="target/targetjars.txt"

echo "--- COMPILING PROJECT ---"
# Build all JARs (main, sources, test-sources, tests)
mvn clean install


echo "--- RETRIEVING DEPENDENCIES ---"
# Get all dependency jars
mvn dependency:build-classpath -Dmdep.outputFile=$classPaths

# Get target directory
TARGET_DIR=$(mvn help:evaluate -Dexpression=project.build.directory -q -DforceStdout)

# Find all JAR files in the target directory and format them as a colon-separated list
jarPaths=$(find "$TARGET_DIR" -name "*.jar" | tr '\n' ':' | sed 's/:$//')

# Save the formatted JAR paths to the output file
echo "$jarPaths" > "$OUTPUT_FILE"

cd "$currentDir" || exit
