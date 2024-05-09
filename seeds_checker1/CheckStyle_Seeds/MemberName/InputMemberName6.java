/*
MemberName
format = ^_[a-z]*$
applyToPublic = false
applyToProtected = false
applyToPackage = false
applyToPrivate = (default)true


*/


public class InputMemberName6 {
  public int mPublic;
  protected int mProtected;
  int mPackage; // comment
  private int mPrivate; // violation

  public int _public;
  protected int _protected;
  int _package;
  private int _private;
}
