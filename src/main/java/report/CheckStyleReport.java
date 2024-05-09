package report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckStyleReport implements Report {

    private String filePath;
    private List<Violation> violations;

    public CheckStyleReport(String filePath) {
        this.filePath = filePath;
        this.violations = new ArrayList<>();
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    public List<Violation> getViolations() {
        return this.violations;
    }

    @Override
    public String toString() {
        return "Path: " + this.filePath + " Size: " + this.violations.size();
    }

    public static List<Report> readResultFile(String reportPath) {
        List<Report> reports = new ArrayList<>();
        HashMap<String, CheckStyleReport> path2report = new HashMap<>();
        File checkFile = new File(reportPath);
        if (!checkFile.exists()) {
            return reports;
        }
        List<String> errorInstances = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(reportPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("[ERROR]") || line.contains("[WARN]")) {
                    errorInstances.add(line);
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filepath;
        for (String errorInstance : errorInstances) {
            int startIndex = errorInstance.indexOf(' ') + 1, endIndex = -1;
            for (int i = startIndex + 1; i < errorInstance.length(); i++) {
                if (errorInstance.charAt(i) == ' ') {
                    endIndex = i;
                    break;
                }
            }
            if (endIndex == -1) {
                return reports;
            }
            String content = errorInstance.substring(startIndex, endIndex);
            int index1 = content.indexOf(".java") + ".java".length(), index2 = -1;
            if (content.charAt(index1) != ':') {
                return reports;
            }
            for (int i = index1 + 1; i < content.length(); i++) {
                if (content.charAt(i) == ':') {
                    index2 = i;
                    break;
                }
            }
            filepath = content.substring(0, index1);
            int row = 0;
            try {
                row = Integer.parseInt(content.substring(index1 + 1, index2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            CheckStyleViolation violation = new CheckStyleViolation(filepath);
            violation.setBeginLine(row);
            index1 = errorInstance.lastIndexOf('[');
            String bugType = errorInstance.substring(index1 + 1, errorInstance.length() - 1);
            violation.setBugType(bugType);
            if (path2report.containsKey(filepath)) {
                path2report.get(filepath).addViolation(violation);
            } else {
                CheckStyleReport report = new CheckStyleReport(filepath);
                report.addViolation(violation);
                reports.add(report);
                path2report.put(filepath, report);
            }
        }
        return reports;
    }

    public static Report readSingleResultFile(String srcPath, String reportPath) {
        File reportFile = new File(reportPath);
        if (!reportFile.exists()) {
            return null;
        }
        Report report = new CheckStyleReport(srcPath);
        List<String> errorInstances = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(reportPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("[ERROR]") || line.contains("[WARN]")) {
                    errorInstances.add(line);
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filepath;
        for (String errorInstance : errorInstances) {
            int startIndex = errorInstance.indexOf(' ') + 1, endIndex = -1;
            for (int i = startIndex + 1; i < errorInstance.length(); i++) {
                if (errorInstance.charAt(i) == ' ') {
                    endIndex = i;
                    break;
                }
            }
            if (endIndex == -1) {
                return report;
            }
            String content = errorInstance.substring(startIndex, endIndex);
            int index1 = content.indexOf(".java") + ".java".length(), index2 = -1;
            if (content.charAt(index1) != ':') {
                return report;
            }
            for (int i = index1 + 1; i < content.length(); i++) {
                if (content.charAt(i) == ':') {
                    index2 = i;
                    break;
                }
            }
            filepath = content.substring(0, index1);
            if (!filepath.equals(srcPath)) {
                System.err.println("Error in Parse CStyle: " + srcPath);
                System.exit(-1);
            }
            int row = 0;
            try {
                row = Integer.parseInt(content.substring(index1 + 1, index2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            CheckStyleViolation violation = new CheckStyleViolation(filepath);
            violation.setBeginLine(row);
            index1 = errorInstance.lastIndexOf('[');
            String bugType = errorInstance.substring(index1 + 1, errorInstance.length() - 1);
            violation.setBugType(bugType);
            report.addViolation(violation);
        }
        return report;
    }

}
