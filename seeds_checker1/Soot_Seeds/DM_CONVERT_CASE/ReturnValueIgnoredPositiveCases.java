import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ReturnValueIgnoredPositiveCases {
  String a = "thing";

  { // String methods
    // BUG: Suggestion includes "remove this line"
    String.format("%d", 10);
    // BUG: Suggestion includes "remove this line"
    String.format("%d", 10).trim();
    // BUG: Suggestion includes "remove this line"
    java.lang.String.format("%d", 10).trim();
    // BUG: Suggestion includes "a = a.intern()"
    a.intern();
    // BUG: Suggestion includes "a = a.trim()"
    a.trim();
    // BUG: Suggestion includes "a = a.trim().concat("b")"
    a.trim().concat("b");
    // BUG: Suggestion includes "a = a.concat("append this")"
    a.concat("append this");
    // BUG: Suggestion includes "a = a.replace('t', 'b')"
    a.replace('t', 'b');
    // BUG: Suggestion includes "a = a.replace("thi", "fli")"
    a.replace("thi", "fli");
    // BUG: Suggestion includes "a = a.replaceAll("i", "b")"
    a.replaceAll("i", "b");
    // BUG: Suggestion includes "a = a.replaceFirst("a", "b")"
    a.replaceFirst("a", "b");
    // BUG: Suggestion includes "a = a.toLowerCase()"
    a.toLowerCase();
    // BUG: Suggestion includes "a = a.toLowerCase(Locale.ENGLISH)"
    a.toLowerCase(Locale.ENGLISH);
    // BUG: Suggestion includes "a = a.toUpperCase()"
    a.toUpperCase();
    // BUG: Suggestion includes "a = a.toUpperCase(Locale.ENGLISH)"
    a.toUpperCase(Locale.ENGLISH);
    // BUG: Suggestion includes "a = a.substring(0)"
    a.substring(0);
    // BUG: Suggestion includes "a = a.substring(0, 1)"
    a.substring(0, 1);
  }

  StringBuffer sb = new StringBuffer("hello");

  {
    // BUG: Suggestion includes "remove this line"
    sb.toString().trim();
  }

  BigInteger b = new BigInteger("123456789");

  { // BigInteger methods
    // BUG: Suggestion includes "b = b.add(new BigInteger("3"))"
    b.add(new BigInteger("3"));
    // BUG: Suggestion includes "b = b.abs()"
    b.abs();
    // BUG: Suggestion includes "b = b.shiftLeft(3)"
    b.shiftLeft(3);
    // BUG: Suggestion includes "b = b.subtract(BigInteger.TEN)"
    b.subtract(BigInteger.TEN);
  }

  BigDecimal c = new BigDecimal("1234.5678");

  { // BigDecimal methods
    // BUG: Suggestion includes "c = c.add(new BigDecimal("1.3"))"
    c.add(new BigDecimal("1.3"));
    // BUG: Suggestion includes "c = c.abs()"
    c.abs();
    // BUG: Suggestion includes "c = c.divide(new BigDecimal("4.5"))"
    c.divide(new BigDecimal("4.5"));
    // BUG: Suggestion includes "remove this line"
    new BigDecimal("10").add(c);
  }
}
