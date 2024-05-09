class Foo2 {
  class InnerClass {
    public InnerClass() {}
  }

  void method() {
    new InnerClass(); // OK, due to public constructor
  }
}
