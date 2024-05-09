/*
IllegalThrows
illegalClassNames = (default)Error, RuntimeException, Throwable, java.lang.Error, \
                    java.lang.RuntimeException, java.lang.Throwable
ignoredMethodNames = (default)finalize
ignoreOverriddenMethods = false


*/


public class InputIllegalThrowsNotIgnoreOverriddenMethod extends InputIllegalThrowsTestDefault {
  @Override
  public void methodTwo() throws RuntimeException { // violation
  }

  @Override
  public java.lang.Throwable methodOne() throws RuntimeException { // violation
    return null;
  }
}
