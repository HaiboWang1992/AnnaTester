package report;

import com.fasterxml.jackson.databind.JsonNode;

public class InferViolation implements Violation {

    private int row, col;
    private String bugType;


    public InferViolation(JsonNode violation) {
        this.row = violation.get("line").asInt();
        this.col = violation.get("column").asInt();
        this.bugType = violation.get("bug_type").asText();
    }

    @Override
    public String getBugType() {
        return this.bugType;
    }

    @Override
    public int getBeginLine() {
        return this.row;
    }

    public int getColumnNumber() {
        return this.col;
    }

}
