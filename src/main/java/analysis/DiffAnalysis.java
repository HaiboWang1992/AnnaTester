package analysis;

import report.Report;
import report.SootReport;
import report.Violation;
import soot.tagkit.Tag;
import util.TriTuple;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static analysis.Schedule.path2bugNum;
import static analysis.TypeWrapper.variant2add;
import static util.Utility.COUNT_DST;
import static util.Utility.COUNT_SRC;
import static util.Utility.DEBUG;
import static util.Utility.DIFFERENTIAL_TESTING;
import static util.Utility.OFFSET_IMPACT;
import static util.Utility.compactIssues;
import static util.Utility.mutant2seed;
import static util.Utility.sep;

public class DiffAnalysis {

    public static void diffAnalysis4DiffChecker(String srcPath, String dstPath, String annotationName,
                                                Map<String, Integer> srcBug2Cnt, Map<String, Integer> dstBug2Cnt) {
        List<String> srcWarnings = new ArrayList<>();
        String initSeedPath = mutant2seed.get(srcPath);
        if (initSeedPath.contains("_Validation")) {
            initSeedPath = initSeedPath.replace("_Validation", "_Seeds");
        }
        File initSeedFile = new File(initSeedPath);
        String key = initSeedFile.getParentFile().getName() + sep + initSeedFile.getName();
        HashMap<String, Integer> bugType2Num = path2bugNum.get(key);
        if (bugType2Num == null) {
            System.out.println("Impact not found: " + initSeedPath);
            return;
        }
        if (DEBUG) {
            System.out.println("Src file report: " + srcPath);
            for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            System.out.println("Dst file report: " + dstPath);
            for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            System.out.println("Impact:");
            for (Map.Entry<String, Integer> entry : bugType2Num.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }
        for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
            int srcBugNum, dstBugNum;
            String bugType = entry.getKey();
            if (!srcBug2Cnt.containsKey(bugType)) {
                srcBugNum = 0;
            } else {
                srcBugNum = srcBug2Cnt.get(bugType);
            }
            dstBugNum = dstBug2Cnt.get(entry.getKey());
            int dv;
            if (bugType2Num.containsKey(bugType)) {
                dv = (dstBugNum - srcBugNum) - bugType2Num.get(bugType);
            } else {
                dv = dstBugNum - srcBugNum;
            }
            if (dv < 0) {
                srcWarnings.add(bugType);
            }
        }
        for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
            String bugType = entry.getKey();
            if (!dstBug2Cnt.containsKey(bugType)) {
                if (bugType2Num.containsKey(bugType)) {
                    if (bugType2Num.get(bugType) + entry.getValue() > 0) {
                        srcWarnings.add(bugType);
                    }
                } else {
                    srcWarnings.add(bugType);
                }
            }
        }
        for (int i = 0; i < srcWarnings.size(); i++) {
            String bugType = srcWarnings.get(i);
            if (!compactIssues.containsKey(bugType)) {
                HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                compactIssues.put(bugType, seq2paths);
            }
            HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
            if (!seq2paths.containsKey(annotationName)) {
                ArrayList<TriTuple> paths = new ArrayList<>();
                seq2paths.put(annotationName, paths);
            }
            List<TriTuple> paths = seq2paths.get(annotationName);
            paths.add(new TriTuple(srcPath, dstPath, "SRC"));
        }
    }

    public static void diffAnalysis4DiffChecker(Report srcReport, Report dstReport, String annotationName) {
        Map<String, Integer> srcBug2Cnt = new HashMap<>();
        Map<String, Integer> dstBug2Cnt = new HashMap<>();
        for (Violation violation : srcReport.getViolations()) {
            if (!srcBug2Cnt.containsKey(violation.getBugType())) {
                srcBug2Cnt.put(violation.getBugType(), 0);
            }
            srcBug2Cnt.put(violation.getBugType(), srcBug2Cnt.get(violation.getBugType()) + 1);
        }
        for (Violation violation : dstReport.getViolations()) {
            if (!dstBug2Cnt.containsKey(violation.getBugType())) {
                dstBug2Cnt.put(violation.getBugType(), 0);
            }
            dstBug2Cnt.put(violation.getBugType(), dstBug2Cnt.get(violation.getBugType()) + 1);
        }
        if (DEBUG) {
            System.out.println("Src file report: " + srcReport.getFilePath());
            for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            System.out.println("Dst file report: " + dstReport.getFilePath());
            for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        }
        List<String> srcWarnings = new ArrayList<>();
        String initSeedPath = mutant2seed.get(srcReport.getFilePath());
        if (initSeedPath.contains("_Validation")) {
            initSeedPath = initSeedPath.replace("_Validation", "_Seeds");
        }
        File initSeedFile = new File(initSeedPath);
        String key = initSeedFile.getParentFile().getName() + sep + initSeedFile.getName();
        HashMap<String, Integer> bugType2Num = path2bugNum.get(key);
        if (bugType2Num == null) {
            System.out.println("Impact not found: " + initSeedPath);
            System.out.println("Report Path: " + srcReport.getFilePath());
            return;
        }
        for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
            int srcBugNum, dstBugNum;
            String bugType = entry.getKey();
            if (!srcBug2Cnt.containsKey(bugType)) {
                srcBugNum = 0;
            } else {
                srcBugNum = srcBug2Cnt.get(bugType);
            }
            dstBugNum = dstBug2Cnt.get(entry.getKey());
            int dv;
            if (bugType2Num.containsKey(bugType)) {
                dv = (dstBugNum - srcBugNum) - bugType2Num.get(bugType);
            } else {
                dv = dstBugNum - srcBugNum;
            }
            if (dv < 0) {
                srcWarnings.add(bugType);
            }
        }
        for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
            String bugType = entry.getKey();
            if (!dstBug2Cnt.containsKey(bugType)) {
                if (bugType2Num.containsKey(bugType)) {
                    if (bugType2Num.get(bugType) + entry.getValue() > 0) {
                        srcWarnings.add(bugType);
                    }
                } else {
                    srcWarnings.add(bugType);
                }
            }
        }
        for (int i = 0; i < srcWarnings.size(); i++) {
            String bugType = srcWarnings.get(i);
            if (!compactIssues.containsKey(bugType)) {
                HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                compactIssues.put(bugType, seq2paths);
            }
            HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
            if (!seq2paths.containsKey(annotationName)) {
                ArrayList<TriTuple> paths = new ArrayList<>();
                seq2paths.put(annotationName, paths);
            }
            List<TriTuple> paths = seq2paths.get(annotationName);
            paths.add(new TriTuple(srcReport.getFilePath(), dstReport.getFilePath(), "SRC"));
        }
    }

    public static void diffAnalysis(String srcPath, String dstPath, String annotationName,
                                    Map<String, Integer> srcBug2Cnt, Map<String, Integer> dstBug2Cnt) {
        if (DIFFERENTIAL_TESTING && OFFSET_IMPACT) {
            diffAnalysis4DiffChecker(srcPath, dstPath, annotationName, srcBug2Cnt, dstBug2Cnt);
            return;
        }
        List<Map.Entry<String, Integer>> srcWarnings = new ArrayList<>();
        List<Map.Entry<String, Integer>> dstWarnings = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
            if (!srcBug2Cnt.containsKey(entry.getKey())) {
                dstWarnings.add(entry);
            } else {
                int srcBugCnt = srcBug2Cnt.get(entry.getKey());
                int dstBugCnt = dstBug2Cnt.get(entry.getKey());
                if (srcBugCnt == dstBugCnt) {
                    continue;
                }
                if (srcBugCnt > dstBugCnt) {
                    srcWarnings.add(entry);
                } else {
                    dstWarnings.add(entry);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
            if (!dstBug2Cnt.containsKey(entry.getKey())) {
                srcWarnings.add(entry);
            }
        }
        if (COUNT_DST) {
            for (int i = 0; i < dstWarnings.size(); i++) {
                String bugType = dstWarnings.get(i).getKey();
                if (!compactIssues.containsKey(bugType)) {
                    HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                    compactIssues.put(bugType, seq2paths);
                }
                HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
                if (!seq2paths.containsKey(annotationName)) {
                    ArrayList<TriTuple> paths = new ArrayList<>();
                    seq2paths.put(annotationName, paths);
                }
                List<TriTuple> paths = seq2paths.get(annotationName);
                paths.add(new TriTuple(srcPath, dstPath, "FP"));
            }
        }
        if (COUNT_SRC) {
            for (int i = 0; i < srcWarnings.size(); i++) {
                String bugType = srcWarnings.get(i).getKey();
                if (!compactIssues.containsKey(bugType)) {
                    HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                    compactIssues.put(bugType, seq2paths);
                }
                HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
                if (!seq2paths.containsKey(annotationName)) {
                    ArrayList<TriTuple> paths = new ArrayList<>();
                    seq2paths.put(annotationName, paths);
                }
                List<TriTuple> paths = seq2paths.get(annotationName);
                paths.add(new TriTuple(srcPath, dstPath, "FN"));
            }
        }
    }

    public static void diffAnalysis(Report srcReport, Report dstReport, String annotationName) {
        if (DIFFERENTIAL_TESTING && OFFSET_IMPACT) {
            diffAnalysis4DiffChecker(srcReport, dstReport, annotationName);
            return;
        }
        Map<String, Integer> srcBug2Cnt = new HashMap<>();
        for (Violation violation : srcReport.getViolations()) {
            String key = violation.getBugType();
            if (!srcBug2Cnt.containsKey(key)) {
                srcBug2Cnt.put(key, 0);
            }
            srcBug2Cnt.put(key, srcBug2Cnt.get(key) + 1);
        }
        Map<String, Integer> dstBug2Cnt = new HashMap<>();
        for (Violation violation : dstReport.getViolations()) {
            String key = violation.getBugType();
            if (!dstBug2Cnt.containsKey(key)) {
                dstBug2Cnt.put(key, 0);
            }
            dstBug2Cnt.put(key, dstBug2Cnt.get(key) + 1);
        }
        List<Map.Entry<String, Integer>> srcWarnings = new ArrayList<>();
        List<Map.Entry<String, Integer>> dstWarnings = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dstBug2Cnt.entrySet()) {
            if (!srcBug2Cnt.containsKey(entry.getKey())) {
                dstWarnings.add(entry);
            } else {
                int srcBugCnt = srcBug2Cnt.get(entry.getKey());
                int dstBugCnt = dstBug2Cnt.get(entry.getKey());
                if (srcBugCnt == dstBugCnt) {
                    continue;
                }
                if (srcBugCnt > dstBugCnt) {
                    srcWarnings.add(entry);
                } else {
                    dstWarnings.add(entry);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : srcBug2Cnt.entrySet()) {
            if (!dstBug2Cnt.containsKey(entry.getKey())) {
                srcWarnings.add(entry);
            }
        }
        if (COUNT_DST) {
            for (int i = 0; i < dstWarnings.size(); i++) {
                String bugType = dstWarnings.get(i).getKey();
                if (!compactIssues.containsKey(bugType)) {
                    HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                    compactIssues.put(bugType, seq2paths);
                }
                HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
                if (!seq2paths.containsKey(annotationName)) {
                    ArrayList<TriTuple> paths = new ArrayList<>();
                    seq2paths.put(annotationName, paths);
                }
                List<TriTuple> paths = seq2paths.get(annotationName);
                paths.add(new TriTuple(srcReport.getFilePath(), dstReport.getFilePath(), "FP"));
            }
        }
        if (COUNT_SRC) {
            for (int i = 0; i < srcWarnings.size(); i++) {
                String bugType = srcWarnings.get(i).getKey();
                if (!compactIssues.containsKey(bugType)) {
                    HashMap<String, List<TriTuple>> seq2paths = new HashMap<>();
                    compactIssues.put(bugType, seq2paths);
                }
                HashMap<String, List<TriTuple>> seq2paths = compactIssues.get(bugType);
                if (!seq2paths.containsKey(annotationName)) {
                    ArrayList<TriTuple> paths = new ArrayList<>();
                    seq2paths.put(annotationName, paths);
                }
                List<TriTuple> paths = seq2paths.get(annotationName);
                paths.add(new TriTuple(srcReport.getFilePath(), dstReport.getFilePath(), "FN"));
            }
        }
    }

    public static void diffAnalysis(SootReport srcReport, SootReport dstReport) {
        String inferType = variant2add.get(dstReport.getFilePath());
        Map<String, List<Tag>> srcType2Tags = srcReport.getType2Annotations();
        Map<String, List<Tag>> dstType2Tags = dstReport.getType2Annotations();
        for(String type : srcType2Tags.keySet()) {
            if(type.equals(inferType)) {
                if(srcType2Tags.get(type).size() + 1 != dstType2Tags.get(type).size()) {
                    System.out.println("Found a Bug");
                    System.out.println(srcReport.getFilePath());
                    System.out.println(dstReport.getFilePath());
                    System.out.println("FN: " + type);
                }
            } else {
                if(srcType2Tags.get(type).size() != dstType2Tags.get(type).size()) {
                    System.out.println("Found a Bug");
                    System.out.println(srcReport.getFilePath());
                    System.out.println(dstReport.getFilePath());
                    System.out.println("FP: " + type);
                }
            }
        }
    }

}
