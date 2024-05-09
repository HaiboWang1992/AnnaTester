class ExampleOk { // OK
  private int a;

  ExampleOk(int a) {
    this.a = a;
  }
}

class ExampleDefaultCtor { // OK
  private String s;

  ExampleDefaultCtor() {
    s = "foobar";
  }
}

class InvalidExample { // violation, class must have a constructor.
  public void test() {}
}

abstract class AbstractExample { // OK
  public abstract void test() {}
}
