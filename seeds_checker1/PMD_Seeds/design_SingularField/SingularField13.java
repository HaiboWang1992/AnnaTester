class Foo {
  private int x;

  int bar(int y) {
    x = y + 5;
    return x;
  }

  private void bar() {
    Foo foo = new Foo();
    foo.x = new Integer(5);
  }
}
