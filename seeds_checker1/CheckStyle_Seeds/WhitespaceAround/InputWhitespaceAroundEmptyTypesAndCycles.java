import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;
import java.util.function.Supplier;

class InputWhitespaceAroundEmptyTypesAndCycles {
  private static final String ALLOWS_NULL_KEYS = "";
  private static final String ALLOWS_NULL_VALUES = "";

  @MapFeature.Require({ALLOWS_NULL_KEYS, ALLOWS_NULL_VALUES})
  private void foo() {
    int i = 0;
    String[][] x = {{"foo"}};
    int len = 0;
    String sequence = null;
    for (int first = 0; first < len && matches(sequence.charAt(first)); first++) {} // 2 violations
    while (i == 1) {} // 2 violations
    do {} while (i == 1); // 2 violations
  }

  private boolean matches(char charAt) {
    return false;
  }
}

interface SupplierFunction<T> extends Function<Supplier<T>, T> {}

class EmptyFoo {}

enum EmptyFooEnum {}

class WithEmptyAnonymous {
  private void foo() {
    MyClass c = new MyClass() {};
  }
}

@Retention(value = RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
@Deprecated
@interface Beta {}

@interface MapFeature {
  @interface Require {

    String[] value();
  }
}

class MyClass {}
