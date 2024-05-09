/*
MethodName
format = (default)^[a-z][a-zA-Z0-9]*$
allowClassName = (default)false
applyToPublic = (default)true
applyToProtected = (default)true
applyToPackage = (default)true
applyToPrivate = false


*/


public interface InputMethodNamePrivateMethodsInInterfaces {

  private void PrivateMethod() {} // ok

  private static void PrivateMethod2() {} // ok

  default void DefaultMethod() { // violation
  }

  public default void DefaultMethod2() { // violation
  }

  void PublicMethod(); // violation

  public void PublicMethod2(); // violation
}
