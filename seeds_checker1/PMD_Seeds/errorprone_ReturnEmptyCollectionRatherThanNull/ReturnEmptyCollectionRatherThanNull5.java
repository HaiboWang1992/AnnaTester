import java.util.Map;

class Foo {
  // Not a good idea...
  public Map<String, String> bar() {
    // ...
    return null;
  }
}
