public class Foo {
  void foo() {
    for (int i = 0, j = 0; i < 42; i++, j++) {
      foo();
    }
  }
}
