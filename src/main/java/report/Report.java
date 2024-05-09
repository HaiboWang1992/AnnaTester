package report;

import java.util.List;

public interface Report {

    String getFilePath();

    void addViolation(Violation newViolation);

    List<Violation> getViolations();

}
