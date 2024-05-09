class Foo {
  private int field;

  class InnerClass {
    private long innerField;

    InnerClass() {
      innerField = field; // violation, accessing a private field
    }
  }
}
