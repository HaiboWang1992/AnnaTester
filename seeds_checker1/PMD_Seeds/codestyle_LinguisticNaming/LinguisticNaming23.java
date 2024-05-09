import java.util.function.Predicate;

class ClassWithPredicates {
  private final Predicate<String> isNotEmpty = string -> !string.isEmpty();
}
