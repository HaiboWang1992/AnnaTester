class Foo {
  private int field;

  class InnerClass {
    private long innerField;

    InnerClass() {
      innerField = Foo.this.field; // violation, accessing a private field
    }
  }
}
