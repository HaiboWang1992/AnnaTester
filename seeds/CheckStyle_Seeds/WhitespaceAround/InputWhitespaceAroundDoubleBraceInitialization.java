import java.util.Properties;

public class InputWhitespaceAroundDoubleBraceInitialization {
  public InputWhitespaceAroundDoubleBraceInitialization() {
    new Properties() {
      {
        setProperty("double curly braces", "are not a style error");
      }
    };
    new Properties() {
      {
        setProperty("", "");
      }
    }; // violation  ''}' is not preceded with whitespace'
    new Properties() {
      {
        setProperty("", ""); // violation ''{' is not followed by whitespace'
      }
    }; // 2 violations below
    new Properties() {
      {
        setProperty("double curly braces", "are not a style error");
      }
    };
    new Properties() {
      {
        setProperty("double curly braces", "are not a style error");
      }

      private int i;
    }; // 2 violations
  }
}
