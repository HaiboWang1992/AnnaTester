class Foo {
  class InnerClass {
    private void secret() {
      Foo.this.outerSecret(); // violation, accessing a private method
    }
  }

  private void outerSecret() {}
}
