import java.util.function.Function;
import java.util.function.Supplier;

public class InputWhitespaceAroundAllowEmptyTypesAndNonEmptyClasses {

  private Object object;

  class SomeClass { // violation ''{' is not preceded with whitespace'
    int a = 5;
  }

  public class CheckstyleTest { // violation ''{' is not preceded with whitespace'
    private static final int SOMETHING = 1;
  }

  class MyClass {
    int a;
  } // violation ''{' is not preceded with whitespace'

  class SomeTestClass {
    int a;
  } // 3 violations

  class TestClass {
    int a;
  }

  int b; // violation ''}' is not followed by whitespace'

  class Table {} // 2 violations

  interface SupplierFunction<T> extends Function<Supplier<T>, T> {} // 2 violations

  class NoMtyCls {
    void foo1() {
      foo2();
    }
  } // violation ''{' is not preceded with whitespace'

  public void foo2() {
    do {} while (true); // 2 violations
  }
}

class EmptyAndNonEmptyClasses { // violation ''{' is not preceded with whitespace'
  int x;
}
