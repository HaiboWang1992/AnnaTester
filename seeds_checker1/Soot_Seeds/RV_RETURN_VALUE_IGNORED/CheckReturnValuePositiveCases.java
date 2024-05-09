import javax.annotation.CheckReturnValue;

/**
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class CheckReturnValuePositiveCases {

  IntValue intValue = new IntValue(0);

  @CheckReturnValue
  private int increment(int bar) {
    return bar + 1;
  }

  public void foo() {
    int i = 1;
    // BUG: Suggestion includes "remove this line"
    increment(i);
    System.out.println(i);
  }

  public void bar() {
    // BUG: Suggestion includes "this.intValue = this.intValue.increment()"
    this.intValue.increment();
  }

  public void testIntValue() {
    IntValue value = new IntValue(10);
    // BUG: Suggestion includes "value = value.increment()"
    value.increment();
  }

  private class IntValue {
    final int i;

    public IntValue(int i) {
      this.i = i;
    }

    @CheckReturnValue
    public IntValue increment() {
      return new IntValue(i + 1);
    }

    public void increment2() {
      // BUG: Suggestion includes "remove this line"
      this.increment();
    }

    public void increment3() {
      // BUG: Suggestion includes "remove this line"
      increment();
    }
  }
}
