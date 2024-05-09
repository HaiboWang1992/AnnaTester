/*
SuperClone


*/


public class InputSuperCloneWithoutWarnings {
  @Override
  protected final Object clone() throws CloneNotSupportedException { // ok
    return new InputSuperCloneWithoutWarnings();
  }
}
