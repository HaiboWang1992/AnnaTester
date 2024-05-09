class Foo {
  class InnerClass {
    private void secret() {}
  }

  private void outerSecret() {
    new InnerClass().secret(); // violation, accessing a private method
  }
}
