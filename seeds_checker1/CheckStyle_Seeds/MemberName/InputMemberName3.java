/*
MemberName
format = ^_[a-z]*$
applyToPublic = (default)true
applyToProtected = false
applyToPackage = false
applyToPrivate = false


*/


public class InputMemberName3 {
  public int mPublic; // violation
  protected int mProtected;
  int mPackage; // comment
  private int mPrivate;

  public int _public;
  protected int _protected;
  int _package;
  private int _private;
}
