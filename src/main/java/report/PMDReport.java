package report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMDReport implements Report {

    private String filePath;
    private List<Violation> violations;

    private PMDReport(String filePath) {
        this.filePath = filePath;
        this.violations = new ArrayList<>();
    }

    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void addViolation(Violation newViolation) {
        this.violations.add(newViolation);
    }

    public List<Violation> getViolations() {
        return this.violations;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("PMD_Report Filename: " + this.filePath + "\n");
        for (Violation pmd_violation : this.violations) {
            out.append(pmd_violation.toString() + "\n");
        }
        return out.toString();
    }

    public Map<String, Integer> type2cnt() {
        Map<String, Integer> res = new HashMap<>();
        for(Violation violation : violations) {
            if(!res.containsKey(violation.getBugType())) {
                res.put(violation.getBugType(), 0);
            }
            res.put(violation.getBugType(), res.get(violation.getBugType()) + 1);
        }
        return res;
    }

    @Override
    public boolean equals(Object rhs) {
        if(rhs instanceof PMDReport) {
            Map<String, Integer> type2cnt1 = this.type2cnt();
            Map<String, Integer> type2cnt2 = ((PMDReport) rhs).type2cnt();
            if(type2cnt1.size() != type2cnt2.size()) {
                return false;
            }
            for(String type : type2cnt1.keySet()) {
                if(!type2cnt2.containsKey(type) || type2cnt1.get(type) != type2cnt2.get(type)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static List<String> errorPMDPaths = new ArrayList<>();

    public static Report readSingleResultFile(final String jsonPath) {
        Report report = null;
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(jsonPath);
        if(!jsonFile.exists()) {
            return null;
        }
        try {
            JsonNode rootNode = mapper.readTree(jsonFile);
            JsonNode reportNodes = rootNode.get("files");
            JsonNode processErrorNode = rootNode.get("processingErrors");
            JsonNode configErrorNode = rootNode.get("configurationErrors");
            if(reportNodes.size() > 0) {
                report = new PMDReport(reportNodes.get(0).get("filename").asText());
            } else {
                if(processErrorNode.size() > 0) {
                    report = new PMDReport(processErrorNode.get(0).get("filename").asText());
                }
            }
            if(processErrorNode.size() > 0 || configErrorNode.size() > 0) {
                errorPMDPaths.add(jsonPath);
                return report;
            }
            for (int i = 0; i < reportNodes.size(); i++) {
                JsonNode reportNode = reportNodes.get(i);
                JsonNode violationNodes = reportNode.get("violations");
                for (int j = 0; j < violationNodes.size(); j++) {
                    JsonNode violationNode = violationNodes.get(j);
                    PMDViolation violation = new PMDViolation(violationNode);
                    report.addViolation(violation);
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("Exceptional Json Path:" + jsonPath);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return report;
    }

    public static List<Report> readResultFile(final String jsonPath) {
        List<Report> reports = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        File jsonFile = new File(jsonPath);
        if(!jsonFile.exists()) {
            return reports;
        }
        try {
            JsonNode rootNode = mapper.readTree(jsonFile);
            JsonNode reportNodes = rootNode.get("files");
            JsonNode processErrorNode = rootNode.get("processingErrors");
            JsonNode configErrorNode = rootNode.get("configurationErrors");
            if(processErrorNode.size() > 0 || configErrorNode.size() > 0) {
                errorPMDPaths.add(jsonPath);
                return reports;
            }
            for (int i = 0; i < reportNodes.size(); i++) {
                JsonNode reportNode = reportNodes.get(i);
                PMDReport newReport = new PMDReport(reportNode.get("filename").asText());
                JsonNode violationNodes = reportNode.get("violations");
                for (int j = 0; j < violationNodes.size(); j++) {
                    JsonNode violationNode = violationNodes.get(j);
                    PMDViolation violation = new PMDViolation(violationNode);
                    newReport.addViolation(violation);
                }
                reports.add(newReport);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Exceptional Json Path:" + jsonPath);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reports;
    }

}
