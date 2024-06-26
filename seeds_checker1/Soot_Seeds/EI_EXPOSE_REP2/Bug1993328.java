import edu.umd.cs.findbugs.annotations.DesireWarning;
import edu.umd.cs.findbugs.annotations.ExpectWarning;

public class Bug1993328 {
  private static Object[] NO_ARGS = new Object[] {};

  private Object[] args1;

  /* ********************
   * Behavior at filing: no warning thrown ********************
   */
  @DesireWarning("EI2")
  public void assign_conditional(Object[] args) {
    this.args1 = args == null ? NO_ARGS : args;
  }
  ;

  /* ********************
   * Behavior at filing: warning thrown => M V EI2:
   * Bug1993328.assign_explicit(Object[]) may expose internal representation
   * by storing an externally mutable object into args1 At
   * Bug1993328.java:[line 15] ********************
   */
  @ExpectWarning("EI2")
  public void assign_explicit(Object[] args) {
    this.args1 = args;
  }
  ;

  // dummy method, eliminates 'unread field' warning
  public boolean hasargs() {
    if (args1 != null) {
      return true;
    }
    return false;
  }
}
