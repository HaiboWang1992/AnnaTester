import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ReturnValueIgnoredNegativeCases {

  private String a = "thing";

  {
    String b = a.trim();
    System.out.println(a.trim());
    new String(new BigInteger(new byte[] {0x01}).add(BigInteger.ONE).toString());
  }

  String run() {
    a.trim().hashCode();
    return a.trim();
  }

  public void methodDoesntMatch() {
    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put("test", 1);
  }

  public void methodDoesntMatch2() {
    final String b = a.toString().trim();
  }
}
