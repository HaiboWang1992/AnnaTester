import java.util.function.Consumer;

class Bar {

  public void foo() {
    Consumer<String> i = (s) -> {};

    Consumer<String> k = (String s) -> {};

    Consumer<String> l = (final String s) -> {};
  }
}
