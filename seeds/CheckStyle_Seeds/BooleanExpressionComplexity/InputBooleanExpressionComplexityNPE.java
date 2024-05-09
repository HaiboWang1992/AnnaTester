/*
BooleanExpressionComplexity
max = (default)3
tokens = (default)LAND, BAND, LOR, BOR, BXOR


*/


public class InputBooleanExpressionComplexityNPE // ok
 {
  static {
    try {
      System.identityHashCode("a");
    } catch (IllegalStateException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }
}
