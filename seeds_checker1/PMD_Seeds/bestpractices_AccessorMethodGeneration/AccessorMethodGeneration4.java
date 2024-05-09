class Foo {
  class InnerClass {
    private void secret() {
      outerSecret(); // violation, accessing a private method
    }
  }

  private void outerSecret() {}
}
