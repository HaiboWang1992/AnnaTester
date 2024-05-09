import static com.puppycrawl.tools.checkstyle.checks.coding.illegaltype.InputIllegalType.SomeStaticClass;
import static com.puppycrawl.tools.checkstyle.utils.CheckUtil.*;
import static com.puppycrawl.tools.checkstyle.utils.CheckUtil.isElseIf;

public class InputIllegalTypeTestStaticImports {
  private boolean foo(String s) {
    return isElseIf(null);
  }

  SomeStaticClass staticClass; // violation

  private static SomeStaticClass foo1() {
    return null;
  }

  private static void foo2(SomeStaticClass s) {} // violation
}
