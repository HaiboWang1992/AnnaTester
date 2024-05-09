import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;
import java.util.function.Supplier;

class InputWhitespaceAroundEmptyTypesAndCycles2 {
  private static final String ALLOWS_NULL_KEYS = "";
  private static final String ALLOWS_NULL_VALUES = "";

  @MapFeature.Require({ALLOWS_NULL_KEYS, ALLOWS_NULL_VALUES})
  private void foo() {
    int i = 0;
    String[][] x = {{"foo"}};
    int len = 0;
    String sequence = null;
    for (int first = 0; first < len && matches(sequence.charAt(first)); first++) {}
    while (i == 1) {}
    do {} while (i == 1);
  }

  private boolean matches(char charAt) {
    return false;
  }
}

interface SupplierFunction2<T> extends Function<Supplier<T>, T> {} // 2 violations

class EmptyFoo2 {} // 2 violations

enum EmptyFooEnum2 {} // 2 violations

class WithEmptyAnonymous2 {
  private void foo() {
    MyClass c = new MyClass() {}; // 2 violations
  }
}

@Retention(value = RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
@Deprecated
@interface Beta2 {} // 2 violations

@interface MapFeature2 {
  @interface Require {

    String[] value();
  }
}

class MyClass2 {}
