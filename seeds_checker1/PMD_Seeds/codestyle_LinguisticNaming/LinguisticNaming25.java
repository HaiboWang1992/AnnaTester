import java.util.function.Predicate;

class SomeClass {
  public Predicate<String> isEmptyPredicate() {
    return String::isEmpty;
  }
}
