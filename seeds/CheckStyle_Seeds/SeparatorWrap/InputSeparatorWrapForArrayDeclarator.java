/*
SeparatorWrap
option = (default)EOL
tokens = ARRAY_DECLARATOR


*/


class InputSeparatorWrapForArrayDeclarator {

  protected int[] arrayDeclarationWithGoodWrapping = new int[] {1, 2};

  protected int[] arrayDeclarationWithBadWrapping =
      new int[] {1, 2}; // violation ''\[' should be on the previous line'
}
