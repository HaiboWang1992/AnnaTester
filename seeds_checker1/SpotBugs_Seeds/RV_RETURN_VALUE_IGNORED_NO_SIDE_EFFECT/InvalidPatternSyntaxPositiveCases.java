import java.util.regex.Pattern;

/**
 * @author mdempsky@google.com (Matthew Dempsky)
 */
public class InvalidPatternSyntaxPositiveCases {
  public static final String INVALID = "*";
  public static final String DOT = ".";

  {
    // BUG: Suggestion includes ""
    Pattern.compile(INVALID);
    // BUG: Suggestion includes ""
    Pattern.compile(INVALID, 0);
    // BUG: Suggestion includes ""
    Pattern.matches(INVALID, "");
    // BUG: Suggestion includes ""
    "".matches(INVALID);
    // BUG: Suggestion includes ""
    "".replaceAll(INVALID, "");
    // BUG: Suggestion includes ""
    "".replaceFirst(INVALID, "");
    // BUG: Suggestion includes ""
    "".split(INVALID);
    // BUG: Suggestion includes ""
    "".split(INVALID, 0);

    // BUG: Suggestion includes ""foo.bar".split("\\.")"
    "foo.bar".split(".");
    // BUG: Suggestion includes ""
    "foo.bonk".split(DOT);
  }
}
