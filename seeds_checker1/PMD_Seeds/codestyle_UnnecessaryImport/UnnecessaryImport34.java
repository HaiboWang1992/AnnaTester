import java.util.*;

/**
 * Note: In order for this test case to work, the class "Issue2016" must also be compiled and
 * available on the auxclasspath.
 */
class Issue2016 {
  public void testFunction() {
    Objects.toString(null);
  }
}
