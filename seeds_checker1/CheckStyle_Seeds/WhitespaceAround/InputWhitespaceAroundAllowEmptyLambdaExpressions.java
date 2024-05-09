import java.util.function.*;

public class InputWhitespaceAroundAllowEmptyLambdaExpressions {
  Runnable noop = () -> {}; // 2 violations
  Runnable noop2 =
      () -> {
        int x = 10;
      };
  BinaryOperator<Integer> sum = (x, y) -> x + y;
  Runnable noop3 =
      () -> {
        ;
      }; // 2 violations
  Runnable noop4 =
      () -> {
        new String();
      }; // 2 violations
}
