import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Preconditions;

public class PreconditionsTooManyArgsNegativeCase1 {
  Integer foo;

  public void checkPositive(int x) {
    checkArgument(x > 0, "%s > 0", x);
  }

  public void checkFoo() {
    Preconditions.checkState(foo.intValue() == 0, "foo must be equal to 0 but was %s", foo);
  }

  public static void checkNotNull(Object foo, String bar, Object baz) {}

  public void checkSelf() {
    checkNotNull(foo, "Foo", this);
  }
}
