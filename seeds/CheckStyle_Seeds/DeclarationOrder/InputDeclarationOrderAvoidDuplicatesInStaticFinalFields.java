/*
DeclarationOrder
ignoreConstructors = (default)false
ignoreModifiers = (default)false


*/


public class InputDeclarationOrderAvoidDuplicatesInStaticFinalFields {
  private boolean allowInSwitchCase;
  public static final String // violation 'Static variable definition in wrong order.'
      MSG_KEY_BLOCK_NESTED = "block.nested";
}
