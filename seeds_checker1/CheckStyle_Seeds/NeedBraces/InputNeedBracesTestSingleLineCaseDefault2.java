/*
NeedBraces
allowSingleLineStatement = true
allowEmptyLoopBody = (default)false
tokens = LITERAL_CASE, LITERAL_DEFAULT


*/


public class InputNeedBracesTestSingleLineCaseDefault2 {
  int value;

  private void main() {
    switch (value) {
      default:
    }
  }

  private void main1() {
    switch (value) {
      case 1:
    }
  }
}

@interface Example {
  String priority() default "value"; // ok
}

interface IntefaceWithDefaultMethod {
  default void doIt() {}
}
