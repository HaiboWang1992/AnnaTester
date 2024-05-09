import java.util.List;

class Foo {
  // Not a good idea...
  public List<String> bar() {
    // ...
    return null;
  }
}
