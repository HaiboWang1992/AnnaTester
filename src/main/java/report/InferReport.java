package report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InferReport implements Report {
    
    private String filePath;
    private List<Violation> violations;

    public InferReport(String filePath) {
        this.filePath = filePath;
        this.violations = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.filePath;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    @Override
    public void addViolation(Violation newViolation) {
        this.violations.add(newViolation);
    }

    @Override
    public List<Violation> getViolations() {
        return this.violations;
    }
    
    public static Report readSingleResultFile(String srcFilepath, File reportFile) {
        InferReport report = new InferReport(srcFilepath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(reportFile);
            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode violationNode = rootNode.get(i);
                InferViolation infer_violation = new InferViolation(violationNode);
                report.addViolation(infer_violation);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return report;
    }

}
