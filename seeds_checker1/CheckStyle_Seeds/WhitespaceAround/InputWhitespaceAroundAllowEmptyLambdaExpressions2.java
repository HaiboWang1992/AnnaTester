import java.util.function.BinaryOperator;

public class InputWhitespaceAroundAllowEmptyLambdaExpressions2 {
  Runnable noop = () -> {};
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
