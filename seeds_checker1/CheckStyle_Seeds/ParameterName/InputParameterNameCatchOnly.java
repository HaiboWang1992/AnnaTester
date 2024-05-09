/*
ParameterName
format = ^NO_WAY_MATEY$
ignoreOverridden = (default)false
accessModifiers = (default)public, protected, package, private


*/


public class InputParameterNameCatchOnly {
  int foo() {
    if (System.currentTimeMillis() > 1000) return 1;

    int test = 0;

    try {
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  public InputParameterNameCatchOnly() // ok
      {
    return;
  }

  class InnerFoo {
    public void fooInnerMethod() // ok
        {}
  }
}
