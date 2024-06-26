package report;

public class CheckStyleViolation implements Violation {

    private String fileName;
    private String bugType;
    private int beginLine;

    public CheckStyleViolation(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setBugType(String bugType) {
        this.bugType = bugType;
    }

    public String getBugType() {
        return this.bugType;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getBeginLine() {
        return this.beginLine;
    }


    @Override
    public String toString() {
        return "File: " + this.fileName + " Line: " + this.beginLine + " Type: " + this.bugType;
    }
}
