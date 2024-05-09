import com.google.common.base.Objects;
import edu.umd.cs.findbugs.annotations.ExpectWarning;

/**
 * @author Bill Pugh (bill.pugh@gmail.com)
 */
public class IncompatibleEqualsPositiveCases {

  @ExpectWarning(value = "EC", num = 7)
  public boolean testEquality(String s, Integer i, Double d, Object a[]) {

    // BUG: Suggestion includes "false"
    if (i.equals(17L)) return true;

    // BUG: Suggestion includes "false"
    if (s.equals(a)) return true;

    // BUG: Suggestion includes "false"
    if (a.equals(s)) return true;

    // BUG: Suggestion includes "false"
    if (i.equals((byte) 17)) return true;

    // BUG: Suggestion includes "false"
    if (s.equals(i)) return true;

    // BUG: Suggestion includes "false"
    if (i.equals(d)) return true;
    // BUG: Suggestion includes "false"
    if (d.equals(a)) return true;

    return false;
  }

  @ExpectWarning(value = "EC", num = 7)
  public boolean testObjectsEquals(String s, Integer i, Double d, Object a[]) {

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(i, 17L)) return true;

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(s, a)) return true;

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(a, s)) return true;

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(i, (byte) 17)) return true;

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(s, i)) return true;

    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(i, d)) return true;
    // BUG: Suggestion includes "false"
    if (java.util.Objects.equals(d, a)) return true;

    return false;
  }

  @ExpectWarning(value = "EC", num = 7)
  public boolean testGuavaEquals(String s, Integer i, Double d, Object a[]) {

    // BUG: Suggestion includes "false"
    if (Objects.equal(i, 17L)) return true;

    // BUG: Suggestion includes "false"
    if (Objects.equal(s, a)) return true;

    // BUG: Suggestion includes "false"
    if (Objects.equal(a, s)) return true;

    // BUG: Suggestion includes "false"
    if (Objects.equal(i, (byte) 17)) return true;

    // BUG: Suggestion includes "false"
    if (Objects.equal(s, i)) return true;

    // BUG: Suggestion includes "false"
    if (Objects.equal(i, d)) return true;
    // BUG: Suggestion includes "false"
    if (Objects.equal(d, a)) return true;

    return false;
  }
}
