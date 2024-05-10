# AnnaTester

AnnaTest is an automated frameork to recognize annotation-induced faults of static analyzers. Basically, it is implemented by annotation synthesis and metamorphic testing techniques. Until now, AnnaTester has found 43 bugs in known static analyzers (PMD, SpotBugs, SonarQube, CheckStyle, Infer, and Soot).

This is the source code repo of AnnaTester. The source code files are located in the folder src, tools and offset folder contains some auxiliary programs, e.g., crawl seed files from web, seed folder contains the downloaded seed files.

# User Guideline

## Project Description
1. Src folder includes source code and test cases for our project.
2. Seeds folder includes the initial input programs for our project.
3. The pom.xml defines the third-party dependency libraries used in our project.

## Install MVN Dependency
We use Maven 3.8.5 to build our project. Users should first install the dependency libraries using the following command:

> mvn -f pom.xml dependency:copy-dependencies

## Install Static Analyzer

Download different static analyzers by the following links:

```bash
PMD: https://github.com/pmd/pmd/releases/tag/pmd_releases/6.51.0
SpotBugs: https://github.com/spotbugs/spotbugs/releases
CheckStyle: https://github.com/checkstyle/checkstyle/releases/
Infer: https://github.com/facebook/infer/releases/tag/v1.1.0
SonarQube: https://www.sonarqube.org/downloads/
Soot: https://github.com/soot-oss/soot/releases
```

## Config property & Execution

Create a config file called "config.properties" in the root folder of AnnaTester, and define the following properties in this file:

```bash
PROJECT_PATH=/PATH/TO/PROJECT
EVALUATION_PATH=/PATH/TO/EVALUATION
SEED_PATH=/PATH/TO/INPUT/PROGRAMS
TOOL_PATH=/PATH/TO/ANALYZERS
JAVAC_PATH=/PATH/TO/JAVAC
TEST_PMD=true
TEST_SPOTBUGS=false
TEST_CHECKSTYLE=false
TEST_INFER=false
TEST_SONARQUBE=false
TEST_SOOT=false
GOOGLE_FORMAT_PATH=/PATH/TO/GOOGLE/FORMAT
PMD_CONFIG_PATH=/PATH/TO/PMD_CONFIG
MOCK_ANNOTATION_JAR_PATH=/PATH/TO/DUMMY_ANNOTATION_JAR
```

Notice that, you should download Google format from [Link](https://github.com/google/google-java-format). The PMD_CONFIG_PATH is designed by users, and we provide one example file in the `tools` folder. MOCK_ANNOTATION_JAR is available via compiling MockAnnotation in `tools\org`.

First use initEnv defined in Utility.java, then use the Schedule.java to select our checkers and test corresponding static analyzers, for instance:

```java
Utility.initEnv();
Schedule schedule = Schedule.getInstance();
schedule.runInjectionChecker();
```

We have provided a shell script for running the project, you can use this file `./run.sh` to assist running AnnaTester.
