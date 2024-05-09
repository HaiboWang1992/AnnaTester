import java.util.function.Function;

public class InputUnnecessaryParenthesesReturnValue {
  int foo() {
    Function<Integer, Integer> addOne =
        x -> {
          return (x + 1);
        }; // violation 'Unnecessary paren.* around return'
    return (1 + 1); // violation 'Unnecessary paren.* around return'
  }

  boolean compare() {
    return (9 <= 3); // violation 'Unnecessary paren.* around return'
  }

  boolean bar() {
    return (true && 7 > 3); // violation 'Unnecessary paren.* around return'
  }

  boolean bigger() {
    return (Integer.parseInt("5") > 7
        || // violation 'Unnecessary paren.* around return'
        Integer.parseInt("2") > 3
        || "null" != null);
  }

  int ternary() {
    return (true ? 0 : 1); // violation 'Unnecessary paren.* around return'
  }

  boolean ok() {
    return 5 > 7 || 6 < 4;
  }

  int okternary() {
    return true ? 0 : 1;
  }
}
