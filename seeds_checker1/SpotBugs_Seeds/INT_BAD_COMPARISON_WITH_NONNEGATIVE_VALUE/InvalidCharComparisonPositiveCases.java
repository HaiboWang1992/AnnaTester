import java.io.IOException;
import java.io.Reader;

/**
 * @author Bill Pugh (bill.pugh@gmail.com)
 */
public class InvalidCharComparisonPositiveCases {

  public boolean testEquality(char c, Reader r) throws IOException {

    // BUG: Suggestion includes "false"
    if (c == -1) return true;

    char d;
    // BUG: Suggestion includes "false"
    if ((d = (char) r.read()) == -1) return true;

    return false;
  }
}
