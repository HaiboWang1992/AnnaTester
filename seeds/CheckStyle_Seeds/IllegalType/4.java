public class Test {
  public HashMap<String, String> function1() { // violation
    // code
  }

  private HashMap<String, String> function2() { // OK
    // code
  }

  protected HashMap<Integer, String> function3() { // violation
    // code
  }

  public static TreeMap<Integer, String> function4() { // violation
    // code
  }
}
