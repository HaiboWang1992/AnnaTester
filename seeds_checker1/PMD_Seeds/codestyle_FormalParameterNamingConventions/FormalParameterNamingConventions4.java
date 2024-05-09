import java.util.function.Consumer;

class Bar {

  void foo(int Foo) {}

  void bar(final int Hoo) {}

  {
    Consumer<String> i = (Koo) -> {};

    Consumer<String> q =
        (KOO) -> { // that's ok
        };

    Consumer<String> k = (String Voo) -> {};

    Consumer<String> l = (final String Ooo) -> {};
  }
}
