class Foo {
  public static class Outer {
    private int field;

    class Inner {
      private long innerField;

      Inner() {
        innerField = Outer.this.field; // violation, accessing a private field
      }
    }
  }
}
