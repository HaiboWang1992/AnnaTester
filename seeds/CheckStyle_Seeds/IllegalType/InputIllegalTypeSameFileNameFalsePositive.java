import com.puppycrawl.tools.checkstyle.checks.coding.illegaltype.InputIllegalTypeGregCal.SubCal;
import java.awt.List;
import java.util.ArrayList;
import java.util.Date;

public class InputIllegalTypeSameFileNameFalsePositive {
  InputIllegalTypeGregCal cal = AnObject.getInstance(); // ok
  Date date = null;
  SubCal subCalendar = null; // violation

  private static class AnObject extends InputIllegalTypeGregCal { // ok

    public static InputIllegalTypeGregCal getInstance() // ok
        {
      return null;
    }
  }

  private void foo() {
    List l; // ok
    java.io.File file = null; // ok
  }

  java.util.List<Integer> list = new ArrayList<>(); // violation
  private ArrayList<String> values; // ok
  private Boolean d; // ok
  private Boolean[] d1;
  private Boolean[][] d2;
}
