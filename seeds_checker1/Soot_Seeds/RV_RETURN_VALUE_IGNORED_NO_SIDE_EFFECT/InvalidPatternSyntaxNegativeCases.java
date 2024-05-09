import java.util.regex.Pattern;

/**
 * @author mdempsky@google.com (Matthew Dempsky)
 */
public class InvalidPatternSyntaxNegativeCases {
  public void foo(String x) {
    Pattern.compile("t");
    Pattern.compile("t", 0);
    Pattern.matches("t", "");
    "".matches("t");
    "".replaceAll("t", "");
    "".replaceFirst("t", "");
    "".split("t");
    "".split("t", 0);

    Pattern.compile(x);
    Pattern.compile(x, 0);
    Pattern.matches(x, "");
    "".matches(x);
    "".replaceAll(x, "");
    "".replaceFirst(x, "");
    "".split(x);
    "".split(x, 0);
  }
}
