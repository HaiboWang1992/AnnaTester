
class StringTrimMethodArgument {
  public String get() {
    return "foo";
  }

  public void bar() {
    String bar = "foo";
    System.out.println(bar.trim().isEmpty()); // violation missing
    System.out.println(bar.trim().length() == 0);
    System.out.println(get().trim().isEmpty()); // violation missing
    System.out.println(get().trim().length() == 0); // violation missing
    System.out.println(this.get().trim().isEmpty()); // violation missing
    System.out.println(this.get().trim().length() == 0); // violating missing
  }
}
