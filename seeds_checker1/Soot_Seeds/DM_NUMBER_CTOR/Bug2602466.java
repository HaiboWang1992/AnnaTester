import edu.umd.cs.findbugs.annotations.ExpectWarning;
import java.util.HashMap;
import java.util.Map;

public class Bug2602466 {

  public Map<Long, String> error_lookup = new HashMap<Long, String>();

  /* ********************
   * Behavior at filing: Bx warning thrown for a call to the Long(long)
   * constructor with a constant value outside the range of values that will
   * be cached (which is currently -128 <= x <= 127) ********************
   */
  // Now reported as a low priority issue
  public void setHighValueKey() {
    error_lookup.put(new Long(0x80000001), "Unknown error.");
  }

  @ExpectWarning("Bx")
  public void setLowValueKey() {
    error_lookup.put(new Long(0), "Success.");
  }
}
