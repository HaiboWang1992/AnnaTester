import java.util.List;

class Foo {
  public static boolean bar(List lst) {
    if (lst.size() != 0) {
      return true;
    }
    return false;
  }
}
