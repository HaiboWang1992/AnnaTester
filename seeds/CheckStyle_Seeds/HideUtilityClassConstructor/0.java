class Test { // violation, class only has a static method and a constructor

  public Test() {}

  public static void fun() {}
}

class Foo { // OK

  private Foo() {}

  static int n;
}

class Bar { // OK

  protected Bar() {
    // prevents calls from subclass
    throw new UnsupportedOperationException();
  }
}

class UtilityClass { // violation, class only has a static field

  static float f;
}
