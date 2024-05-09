/*
IllegalTokenText
format = a href
ignoreCase = (default)false
message = (default)
tokens = STRING_LITERAL


*/


/** Test for illegal tokens */
public class InputIllegalTokenTextTokens {
  public void methodWithPreviouslyIllegalTokens() {
    int i = 0;
    switch (i) {
      default:
        i--;
        i++;
        break;
    }
  }

  public native void nativeMethod();

  public void methodWithLiterals() {
    final String ref = "<a href=\""; // violation
    final String refCase = "<A hReF=\"";
  }

  public void methodWithLabels() {
    label:
    {
      anotherLabel: // some comment href
      do {
        continue anotherLabel;
      } while (false);
      break label; // some a href
    }
  }
}
