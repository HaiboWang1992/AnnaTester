import java.util.List;

class Foo {
  public static boolean bar(List lst) {
    if (0 == lst.size()) {
      return true;
    }
    return false;
  }
}
