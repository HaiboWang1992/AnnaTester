
class UseEqualsToCompareStringsSample {
  void bar(String x) {
    if (x == "hello") {}
    if (x == new String("hello")) {}
  }
}
