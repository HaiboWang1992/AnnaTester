import java.util.Collections;
import java.util.List;

class Foo {
  // Not a good idea...
  public List<String> bar() {
    // ...
    return Collections.emptyList();
  }
}
