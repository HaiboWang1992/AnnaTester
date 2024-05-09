import java.util.function.Function;

class InputJavadocTagContinuationIndentationGuavaFalsePositive {

  /**
   * This class implements the GWT serialization of {@link HashMultimap}. // ok
   *
   * @author Jord Sonneveld
   */
  public static <T extends Enum<T>> Function<String, T> valueOfFunction(Class<T> enumClass) {
    return null;
  }
}
