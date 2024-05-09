/*
RightCurly
option = ALONE
tokens = CLASS_DEF


*/


class InputRightCurlyTestSingleLineClass {
  void foo() throws InterruptedException {

    try {

    } // ok
    catch (Exception e) {
      return;
    }
  }
}

class UniqEmptyClassTestSingleLineClass {
  private int a;
} // violation
