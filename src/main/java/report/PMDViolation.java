package report;

import com.fasterxml.jackson.databind.JsonNode;

public class PMDViolation implements Violation {

    public int beginLine;
    public int endLine;
    public int beginCol;
    public int endCol;
    public String bugType;
    public String description;

    public PMDViolation(JsonNode reportNode) {
        this.beginLine = reportNode.get("beginline").asInt();
        this.endLine = reportNode.get("endline").asInt();
        this.beginCol = reportNode.get("begincolumn").asInt() - 1;
        this.endCol = reportNode.get("endcolumn").asInt() + 1;
        this.bugType = reportNode.get("rule").asText();
        this.description = reportNode.get("ruleset") + ":[" + reportNode.get("rule") + ", " + reportNode.get("description").toString() + "]";
    }

    @Override
    public String getBugType() {
        return this.bugType;
    }

    @Override
    public int getBeginLine() {
        return this.beginLine;
    }

    @Override
    public String toString() {
        return "Violation:" + this.bugType + " start at line [" + this.beginLine + "]";
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PMDViolation) {
            PMDViolation rhs = (PMDViolation) o;
            if(rhs.beginLine == this.beginLine && rhs.beginCol == this.beginCol
                    && rhs.endLine == this.endLine && rhs.endCol == this.endCol
                    && rhs.bugType == this.bugType) {
                return true;
            }
        }
        return false;
    }

}
