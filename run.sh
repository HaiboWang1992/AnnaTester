#!/bin/bash
echo "Building process start..."

JAR_PATH=/PATH/TO/DEPENDENCY
BIN_PATH=bin
SRC_PATH=/PATH/TO/SRC

# Java source code list, used to manage code files
SRC_FILE_LIST_PATH=src/sources.list

# Used to generate java source code list
rm -f $SRC_PATH/sources.list
find $SRC_PATH/ -name *.java > $SRC_FILE_LIST_PATH

# Remove old compiled files and generate bin directory
rm -rf $BIN_PATH/
mkdir $BIN_PATH/

# Generate jar dependency list
for file in  ${JAR_PATH}/*.jar;
do
jarfile=${jarfile}:${file}
done
# echo "jarfile = "$jarfile

# Compile
javac -encoding UTF-8 -d $BIN_PATH/ -cp $jarfile @$SRC_FILE_LIST_PATH

java -cp $BIN_PATH$jarfile MainClassName &
