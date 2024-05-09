import java.util.function.Predicate;

class SomeClass {
  public static void main(String[] args) {
    Predicate<String> isEmpty = String::isEmpty;
  }
}
