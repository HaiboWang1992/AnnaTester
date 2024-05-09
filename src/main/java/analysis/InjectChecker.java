package analysis;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import org.apache.commons.io.FileUtils;
import report.CheckStyleReport;
import report.InferReport;
import report.PMDReport;
import report.Report;
import report.SonarQubeReport;
import report.SootReport;
import report.SpotBugsReport;
import util.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static analysis.DiffAnalysis.diffAnalysis;
import static util.Utility.MOCK_ANNOTATION_JAR_PATH;
import static util.Utility.TEST_CHECKSTYLE;
import static util.Utility.CHECKSTYLE_PATH;
import static util.Utility.EVALUATION_PATH;
import static util.Utility.TEST_INFER;
import static util.Utility.INFER_PATH;
import static util.Utility.JAVA_PATH;
import static util.Utility.PMD_CONFIG_PATH;
import static util.Utility.TEST_PMD;
import static util.Utility.PROJECT_PATH;
import static util.Utility.TEST_SONARQUBE;
import static util.Utility.SONARQUBE_PROJECT_KEY;
import static util.Utility.SONAR_SCANNER_PATH;
import static util.Utility.TEST_SOOT;
import static util.Utility.TEST_SPOTBUGS;
import static util.Utility.SPOTBUGS_PATH;
import static util.Utility.compileJavaSourceFile;
import static util.Utility.createSonarQubeProject;
import static util.Utility.deleteSonarQubeProject;
import static util.Utility.path_sep;
import static util.Utility.inferDependencyJarStr;
import static util.Utility.invokeCommandsByZT;
import static util.Utility.invokeCommandsByZTWithOutput;
import static util.Utility.sep;
import static util.Utility.waitTaskEnd;
import static util.Utility.writeLinesToFile;

public class InjectChecker {

    private static Map<TypeWrapper, List<TypeWrapper>> head2variants = new HashMap<>();

    public static void run(List<String> seedPaths, List<AnnotationWrapper> annotations) {
        List<TypeWrapper> srcWrappers = new ArrayList<>();
        for (int i = 0; i < seedPaths.size(); i++) {
            String seedPath = seedPaths.get(i);
            TypeWrapper initSeed = new TypeWrapper(seedPath);
            srcWrappers.add(initSeed);
        }
        List<TypeWrapper> initWrappers = new ArrayList<>();
        for(int i = 0; i < srcWrappers.size(); i++) {
            TypeWrapper head = srcWrappers.get(i);
            List<TypeWrapper> mutants = new ArrayList<>();
            for (int j = 0; j < annotations.size(); j++) {
                AnnotationWrapper annotation = annotations.get(j);
                if(TEST_SOOT) {
                    mutants.addAll(head.transformByAnnotationSyntax(annotation));
                } else {
                    mutants.addAll(head.transformByAnnotationInsertion(annotation));
                }
            }
            if (!head2variants.containsKey(head)) {
                head2variants.put(head, new ArrayList<>());
            }
            head2variants.get(head).addAll(mutants);
            initWrappers.addAll(mutants);
        }
        System.out.println("Init Wrapper Size: " + initWrappers.size());
        if(TEST_PMD) {
            runPMD();
        }
        if(TEST_SPOTBUGS) {
            runSpotBugs();
        }
        if(TEST_CHECKSTYLE) {
            runCheckStyle();
        }
        if(TEST_INFER) {
            runInfer();
        }
        if(TEST_SONARQUBE) {
            runSonarQube();
        }
        if(TEST_SOOT) {
            runSoot();
        }
    }

    public static void runPMD() {
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            int srcBugCnt, dstBugCnt;
            TypeWrapper srcWrapper = entry.getKey();
            Path srcReportPath = Paths.get(EVALUATION_PATH + sep + "results" + sep + srcWrapper.getFileName() + ".json");
            Path srcDetectionPath = Paths.get(srcWrapper.getFilePath());
            PMDConfiguration srcConfig = new PMDConfiguration();
            srcConfig.setReportFormat("json");
            srcConfig.setReportFile(srcReportPath);
            srcConfig.addRuleSet(PMD_CONFIG_PATH);
            srcConfig.setIgnoreIncrementalAnalysis(true);
            PmdAnalysis srcAnalysis = PmdAnalysis.create(srcConfig);
            srcAnalysis.files().addFile(srcDetectionPath);
            srcAnalysis.performAnalysis();
            List<Report> reportList = PMDReport.readResultFile(srcReportPath.toFile().getAbsolutePath());
            if (reportList.size() > 1) {
                System.err.println("Not expected report list size! [1]");
                System.err.println("Error report path: " + srcReportPath);
                System.exit(-1);
            }
            Report srcReport = null, dstReport = null;
            if (reportList.size() == 0) {
                File srcReportFile = srcReportPath.toFile();
                if (!srcReportFile.exists()) {
                    System.err.println("[1] RT Fail to run PMD: " + srcReportPath);
                    System.out.println("Check file: " + srcWrapper.getFilePath());
                    continue;
                }
                srcBugCnt = 0;
            } else {
                srcReport = reportList.get(0);
                srcBugCnt = srcReport.getViolations().size();
            }
            for (TypeWrapper initWrapper : entry.getValue()) {
                Path dstReportPath = Paths.get(EVALUATION_PATH + sep + "results" + sep + initWrapper.getFileName() + ".json");
                Path dstDetectionPath = Paths.get(initWrapper.getFilePath());
                PMDConfiguration dstConfig = new PMDConfiguration();
                dstConfig.setReportFormat("json");
                dstConfig.setReportFile(dstReportPath);
                dstConfig.addRuleSet(PMD_CONFIG_PATH);
                dstConfig.setIgnoreIncrementalAnalysis(true);
                PmdAnalysis dstAnalysis = PmdAnalysis.create(dstConfig);
                dstAnalysis.files().addFile(dstDetectionPath);
                dstAnalysis.performAnalysis();
                reportList = PMDReport.readResultFile(dstReportPath.toFile().getAbsolutePath());
                if (reportList.size() > 1) {
                    System.err.println("Not expected report list size! [2]");
                    System.err.println("Error report path: " + dstReportPath);
                    System.exit(-1);
                }
                if (reportList.size() == 0) {
                    File dstReportFile = dstReportPath.toFile();
                    if (!dstReportFile.exists()) {
                        System.err.println("[2] RT Fail to run PMD: " + dstReportPath);
                        System.err.println("Check path: " + initWrapper.getFilePath());
                        continue;
                    }
                    dstBugCnt = 0;
                } else {
                    dstReport = reportList.get(0);
                    dstBugCnt = dstReport.getViolations().size();
                }
                if (srcBugCnt == dstBugCnt) {
                    continue;
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport, initWrapper.getInsertedAnnotationWrapper().getAnnotationName());
                }
            }
        }
    }

    public static void runSpotBugs() {
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            int srcBugCnt = 0, dstBugCnt;
            TypeWrapper srcWrapper = entry.getKey();
            File srcClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + srcWrapper.getFileName());
            if (!srcClassFolder.exists()) {
                srcClassFolder.mkdir();
            }
            String srcReportPath = EVALUATION_PATH + sep + "results" + sep + srcWrapper.getFileName() + ".xml";
            if (!compileJavaSourceFile(srcWrapper.getFolderPath(), srcWrapper.getFileName() + ".java", srcClassFolder.getAbsolutePath())) {
                continue;
            }
            String[] invokeSrcCommands = new String[3];
            invokeSrcCommands[0] = "/bin/bash";
            invokeSrcCommands[1] = "-c";
            invokeSrcCommands[2] = SPOTBUGS_PATH + " -textui"
                    + " -xml:withMessages" + " -output " + srcReportPath + " " + srcClassFolder.getAbsolutePath();
            invokeCommandsByZT(invokeSrcCommands);
            File srcReportFile = new File(srcReportPath);
            if(!srcReportFile.exists()) {
                continue;
            }
            Report srcReport = null, dstReport = null;
            List<Report> reportList = SpotBugsReport.readResultFile(srcWrapper.getFolderPath(), srcReportPath);
            if (reportList.size() == 0) {
                srcBugCnt = 0;
            } else {
                srcReport = reportList.get(0);
                srcBugCnt += srcReport.getViolations().size();
            }
            for (TypeWrapper dstWrapper : entry.getValue()) {
                String dstReportPath = EVALUATION_PATH + sep + "results" + sep + dstWrapper.getFileName() + ".xml";
                File dstClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + dstWrapper.getFileName());
                if (!dstClassFolder.exists()) {
                    dstClassFolder.mkdir();
                }
                if (!compileJavaSourceFile(dstWrapper.getFolderPath(), dstWrapper.getFileName() + ".java", dstClassFolder.getAbsolutePath())) {
                    continue;
                }
                String[] invokeDstCommands = new String[3];
                invokeDstCommands[0] = "/bin/bash";
                invokeDstCommands[1] = "-c";
                invokeDstCommands[2] = SPOTBUGS_PATH + " -textui"
                        + " -xml:withMessages" + " -output " + dstReportPath + " " + dstClassFolder.getAbsolutePath();
                invokeCommandsByZT(invokeDstCommands);
                reportList = SpotBugsReport.readResultFile(dstWrapper.getFolderPath(), dstReportPath);
                if (reportList.size() == 0) {
                    dstBugCnt = 0;
                } else {
                    dstReport = reportList.get(0);
                    dstBugCnt = dstReport.getViolations().size();
                }
                if (srcBugCnt == dstBugCnt) {
                    continue;
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport, dstWrapper.getInsertedAnnotationWrapper().getAnnotationName());
                }
            }
        }
    }

    public static void runCheckStyle() {
        String configPath = PROJECT_PATH + sep + "tools" + sep + "google_check.xml";
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            int srcBugCnt, dstBugCnt;
            TypeWrapper srcWrapper = entry.getKey();
            String srcReportPath = EVALUATION_PATH + sep + "results" + sep + srcWrapper.getFileName() + ".txt";
            String[] invokeSrcCommands = new String[3];
            invokeSrcCommands[0] = "/bin/bash";
            invokeSrcCommands[1] = "-c";
            invokeSrcCommands[2] = JAVA_PATH + " -jar " + CHECKSTYLE_PATH + " -f" + " plain" + " -o " + srcReportPath + " -c " + configPath + " " + srcWrapper.getFilePath();
            invokeCommandsByZT(invokeSrcCommands);
            File srcReportFile = new File(srcReportPath);
            if(!srcReportFile.exists()) {
                continue;
            }
            List<Report> reportList = CheckStyleReport.readResultFile(srcReportPath);
            Report srcReport = null, dstReport = null;
            if (reportList.size() == 0) {
                srcBugCnt = 0;
            } else {
                srcReport = reportList.get(0);
                srcBugCnt = srcReport.getViolations().size();
            }
            for (TypeWrapper dstWrapper : entry.getValue()) {
                String dstReportPath = EVALUATION_PATH + sep + "results" + sep + dstWrapper.getFileName() + ".txt";
                String[] invokeDstCommands = new String[3];
                invokeDstCommands[0] = "/bin/bash";
                invokeDstCommands[1] = "-c";
                invokeDstCommands[2] = JAVA_PATH + " -jar " + CHECKSTYLE_PATH + " -f" + " plain" + " -o " + dstReportPath + " -c " + configPath + " " + dstWrapper.getFilePath();
                invokeCommandsByZT(invokeDstCommands);
                File dstReportFile = new File(dstReportPath);
                if (!dstReportFile.exists()) {
                    continue;
                }
                reportList = CheckStyleReport.readResultFile(dstReportPath);
                if (reportList.size() == 0) {
                    dstBugCnt = 0;
                } else {
                    dstReport = reportList.get(0);
                    dstBugCnt = dstReport.getViolations().size();
                }
                if (srcBugCnt == dstBugCnt) {
                    continue;
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport, dstWrapper.getInsertedAnnotationWrapper().getAnnotationName());
                }
            }
        }
    }

    public static void runInfer() {
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            TypeWrapper srcWrapper = entry.getKey();
            String srcFileName = srcWrapper.getFileName();
            File srcReportFolder = new File(EVALUATION_PATH + sep + "results" + sep + srcFileName);
            File srcClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + srcFileName);
            String srcDetectionPath = srcWrapper.getFilePath();
            String srcCmd = INFER_PATH + " run -o " + srcReportFolder.getAbsolutePath() + " -- javac " +
                    " -d " + srcClassFolder.getAbsolutePath() + sep + srcFileName +
                    " -cp " + inferDependencyJarStr + path_sep + MOCK_ANNOTATION_JAR_PATH + " " + srcDetectionPath;
            String[] srcInvokeCommands = {"/bin/bash", "-c", srcCmd};
            invokeCommandsByZT(srcInvokeCommands);
            File srcReportFile = new File(srcReportFolder.getAbsolutePath() + sep + "report.json");
            if(!srcReportFile.exists()) {
                try {
                    FileUtils.deleteDirectory(srcReportFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Report srcReport = InferReport.readSingleResultFile(srcWrapper.getFilePath(), srcReportFile);
            try {
                File newSrcReportFile = new File(EVALUATION_PATH + sep + "results" + sep + srcReportFolder.getName() + ".json");
                FileUtils.moveFile(srcReportFile, newSrcReportFile);
                FileUtils.deleteDirectory(srcReportFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (TypeWrapper dstWrapper : entry.getValue()) {
                String dstFileName = dstWrapper.getFileName();
                File dstReportFolder = new File(EVALUATION_PATH + sep + "results" + sep + dstFileName);
                File dstClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + dstFileName);
                String dstDetectionPath = dstWrapper.getFilePath();
                String dstCmd = INFER_PATH + " run -o " + dstReportFolder.getAbsolutePath() + " -- javac " +
                        " -d " + dstClassFolder.getAbsolutePath() + sep + dstFileName +
                        " -cp " + inferDependencyJarStr + path_sep + MOCK_ANNOTATION_JAR_PATH + " " + dstDetectionPath;
                String[] dstInvokeCommands = {"/bin/bash", "-c", dstCmd};
                invokeCommandsByZT(dstInvokeCommands);
                File dstReportFile = new File(dstReportFolder.getAbsolutePath() + sep + "report.json");
                if(!dstReportFile.exists()) {
                    try {
                        File newDstReportFile = new File(EVALUATION_PATH + sep + "results" + sep + dstReportFolder.getName() + ".json");
                        System.out.println("newDst Path: " + newDstReportFile.getAbsolutePath());
                        FileUtils.moveFile(dstReportFile, newDstReportFile);
                        FileUtils.deleteDirectory(dstReportFolder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Report dstReport = InferReport.readSingleResultFile(dstWrapper.getFilePath(), dstReportFile);
                try {
                    File newDstReportFile = new File(EVALUATION_PATH + sep + "results" + sep + dstReportFolder.getName() + ".json");
                    FileUtils.moveFile(dstReportFile, newDstReportFile);
                    FileUtils.deleteDirectory(dstReportFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (srcReport.getViolations().size() == dstReport.getViolations().size()) {
                    continue;
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport, dstWrapper.getInsertedAnnotationWrapper().getAnnotationName());
                }
            }
        }
    }

    public static void runSonarQube() {
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            TypeWrapper srcWrapper = entry.getKey();
            String[] invokeCommands = new String[3];
            deleteSonarQubeProject(SONARQUBE_PROJECT_KEY);
            createSonarQubeProject(SONARQUBE_PROJECT_KEY);
            invokeCommands[0] = "/bin/bash";
            invokeCommands[1] = "-c";
            invokeCommands[2] = SONAR_SCANNER_PATH
                    + " -Dsonar.projectKey=" + SONARQUBE_PROJECT_KEY
                    + " -Dsonar.projectBaseDir=" + PROJECT_PATH
                    + " -Dsonar.sources=" + srcWrapper.getFilePath()
                    + " -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=123456";
            if(invokeCommandsByZT(invokeCommands)) {
                waitTaskEnd();
            } else {
                continue;
            }
            String[] curlCommands = new String[4];
            curlCommands[0] = "curl";
            curlCommands[1] = "-u";
            curlCommands[2] = "admin:123456";
            curlCommands[3] = "http://localhost:9000/api/issues/search?p=1&ps=500&componentKeys=" + SONARQUBE_PROJECT_KEY;
            String output = invokeCommandsByZTWithOutput(curlCommands);
            writeLinesToFile(output, EVALUATION_PATH + sep + "results" + sep + srcWrapper.getFileName() + ".json");
            Report srcReport = SonarQubeReport.readSingleResultFile(srcWrapper.getFilePath(), output);
            for (TypeWrapper dstWrapper : entry.getValue()) {
                deleteSonarQubeProject(SONARQUBE_PROJECT_KEY);
                createSonarQubeProject(SONARQUBE_PROJECT_KEY);
                invokeCommands[0] = "/bin/bash";
                invokeCommands[1] = "-c";
                invokeCommands[2] = SONAR_SCANNER_PATH
                        + " -Dsonar.projectKey=" + SONARQUBE_PROJECT_KEY
                        + " -Dsonar.projectBaseDir=" + EVALUATION_PATH
                        + " -Dsonar.sources=" + dstWrapper.getFilePath()
                        + " -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin -Dsonar.password=123456";
                if(invokeCommandsByZT(invokeCommands)) {
                    waitTaskEnd();
                } else {
                    continue;
                }
                curlCommands = new String[4];
                curlCommands[0] = "curl";
                curlCommands[1] = "-u";
                curlCommands[2] = "admin:123456";
                curlCommands[3] = "http://localhost:9000/api/issues/search?p=1&ps=500&componentKeys=" + SONARQUBE_PROJECT_KEY;
                output = invokeCommandsByZTWithOutput(curlCommands);
                writeLinesToFile(output, EVALUATION_PATH + sep + "results" + sep + dstWrapper.getFileName() + ".json");
                SonarQubeReport dstReport = SonarQubeReport.readSingleResultFile(dstWrapper.getFilePath(), output);
                if(Utility.DEBUG) {
                    if(srcReport == null) {
                        System.out.println("Not exist Src Report: " + srcReport.getFilePath());
                    }
                    if(dstReport == null) {
                        System.out.println("Not exist Dst Report: " + dstReport.getFilePath());
                    }
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport, dstWrapper.getInsertedAnnotationWrapper().getAnnotationName());
                }
            }
        }
    }

    public static void runSoot() {
        for (Map.Entry<TypeWrapper, List<TypeWrapper>> entry : head2variants.entrySet()) {
            TypeWrapper srcWrapper = entry.getKey();
            File srcClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + srcWrapper.getFileName());
            if(!srcClassFolder.exists()) {
                srcClassFolder.mkdir();
            }
            if (!compileJavaSourceFile(srcWrapper.getFolderPath(), srcWrapper.getFileName() + ".java", srcClassFolder.getAbsolutePath())) {
                continue;
            }
            if(Utility.DEBUG) {
                System.out.println("Invoke Soot Analyze: " + srcClassFolder.getAbsolutePath());
            }
            SootReport srcReport = new SootReport(srcWrapper.getFilePath(), srcClassFolder.getAbsolutePath());
            for (TypeWrapper dstWrapper : entry.getValue()) {
                File dstClassFolder = new File(EVALUATION_PATH + sep + "classes" + sep + dstWrapper.getFileName());
                if (!dstClassFolder.exists()) {
                    dstClassFolder.mkdir();
                }
                if (!compileJavaSourceFile(dstWrapper.getFolderPath(), dstWrapper.getFileName() + ".java", dstClassFolder.getAbsolutePath())) {
                    continue;
                }
                SootReport dstReport = new SootReport(dstWrapper.getFilePath(), dstClassFolder.getAbsolutePath());
                if(Utility.DEBUG) {
                    System.out.println("Invoke Soot Analyze: " + dstClassFolder.getAbsolutePath());
                    if(srcReport == null) {
                        System.out.println("Not exist Src Report: " + srcReport.getFilePath());
                    }
                    if(dstReport == null) {
                        System.out.println("Not exist Dst Report: " + dstReport.getFilePath());
                    }
                }
                if (srcReport != null && dstReport != null) {
                    diffAnalysis(srcReport, dstReport);
                }
            }
        }
    }

}
