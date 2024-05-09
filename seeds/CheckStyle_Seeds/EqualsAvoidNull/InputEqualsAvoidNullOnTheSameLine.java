/*
EqualsAvoidNull
ignoreEqualsIgnoreCase = (default)false


*/


public class InputEqualsAvoidNullOnTheSameLine {

  static {
    String b = "onion";
    String a = b;
    a.equals("ONION"); // violation 'left .* of .* equals'
  }

  private String a = "";
  private A b = null;

  public void shouldWarn() {
    a.equals("");
    A a = b; // violation 'left .* of .* equals'
  }

  public void shouldNotWarn() {
    A a = b;
    a.equals("");
  }

  class A {}
}
