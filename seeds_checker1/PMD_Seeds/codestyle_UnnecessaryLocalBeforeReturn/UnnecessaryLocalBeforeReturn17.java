import java.util.concurrent.Callable;

class UnnecessaryLocal {
  void foo() {
    Callable<String> c =
        () -> {
          String s = "1";
          return s;
        };
  }
}
