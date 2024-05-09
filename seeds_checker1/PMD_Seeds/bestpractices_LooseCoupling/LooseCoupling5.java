import java.util.HashSet;

class Foo {
  HashSet fooSet = new HashSet(); // NOT OK

  HashSet foo() { // NOT OK
    return fooSet;
  }
}
