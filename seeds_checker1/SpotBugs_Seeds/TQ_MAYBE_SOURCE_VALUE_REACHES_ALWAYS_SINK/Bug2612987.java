import edu.umd.cs.findbugs.annotations.DesireNoWarning;
import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;

public class Bug2612987 {
  @Nonnegative private int nonNegativeValue = 1;

  @Nonnegative
  public int get() {
    return nonNegativeValue;
  }

  /* ********************
   * Behavior at filing: TQ warning thrown for explicitly checked (and
   * annotated) parameter ********************
   */
  @DesireNoWarning("TQ")
  public void set(@CheckForSigned int possibleNegativeValue) {
    if (possibleNegativeValue >= 0) nonNegativeValue = possibleNegativeValue;
  }
}
