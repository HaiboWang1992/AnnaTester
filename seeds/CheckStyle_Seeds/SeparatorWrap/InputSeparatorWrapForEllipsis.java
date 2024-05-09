/*
SeparatorWrap
option = (default)EOL
tokens = ELLIPSIS


*/


class InputSeparatorWrapForEllipsis {

  public void testMethodWithGoodWrapping(String... parameters) {}

  public void testMethodWithBadWrapping(
      String... parameters) { // violation ''...' should be on the previous line'
  }
}
