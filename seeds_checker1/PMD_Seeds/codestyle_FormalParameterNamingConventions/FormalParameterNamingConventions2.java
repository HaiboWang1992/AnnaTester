import java.util.function.Consumer;

class Bar {

  void foo(int Foo) {}

  void fooBar(int FOO) { // that's ok
  }

  void bar(final int Hoo) {}

  {
    Consumer<String> i = (Koo) -> {};

    Consumer<String> k = (String Voo) -> {};

    Consumer<String> l = (final String Ooo) -> {};
  }
}
