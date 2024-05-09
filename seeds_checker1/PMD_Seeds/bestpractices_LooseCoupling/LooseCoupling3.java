import java.util.*;

class Foo {
  Set fooSet = new HashSet(); // OK

  Set foo() {
    return fooSet;
  }
}
