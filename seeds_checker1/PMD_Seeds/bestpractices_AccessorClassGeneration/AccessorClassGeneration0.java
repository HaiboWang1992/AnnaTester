class Foo1 {
  class InnerClass {
    private InnerClass() {}
  }

  void method() {
    new InnerClass(); // Causes generation of accessor
  }
}
