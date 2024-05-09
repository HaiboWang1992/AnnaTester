class Foo3 {
  class InnerClass {
    void method() {
      new Foo3(); // Causes generation of accessor
    }
  }

  private Foo3() {}
}
