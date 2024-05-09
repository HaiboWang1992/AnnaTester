/*
ModifiedControlVariable
skipEnhancedForLoopVariable = true


*/


public class InputModifiedControlVariableEnhancedForLoopVariable { // ok

  public void method2() {
    final String[] lines = {"line1", "line2", "line3"};
    for (String line : lines) {
      line = line.trim();
    }
  }
}
