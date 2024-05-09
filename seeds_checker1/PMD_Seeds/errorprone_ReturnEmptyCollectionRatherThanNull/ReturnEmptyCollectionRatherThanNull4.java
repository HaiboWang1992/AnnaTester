import java.util.Set;

class Foo {
  // Not a good idea...
  public Set<String> bar() {
    // ...
    return null;
  }
}
