/*
IllegalCatch
illegalClassNames = java.lang.Error, java.lang.Exception, java.lang.Throwable


*/


public class InputIllegalCatch3 {
  public void foo() {
    try { // class names
    } catch (RuntimeException e) {
    } catch (Exception e) { // violation
    } catch (Throwable e) { // violation
    }
  }

  public void bar() {
    try {
      /* fully qualified class names */
    } catch (java.lang.RuntimeException e) {
    } catch (java.lang.Exception e) { // violation
    } catch (java.lang.Throwable e) { // violation
    }
  }
}
