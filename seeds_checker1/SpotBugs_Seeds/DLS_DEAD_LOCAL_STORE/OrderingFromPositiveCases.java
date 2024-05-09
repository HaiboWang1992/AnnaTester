import com.google.common.collect.Ordering;
import java.util.Comparator;

/**
 * Positive test cases for theOrdering.from(new Comparator&lt;T&gt;() { ... }) check
 *
 * @author sjnickerson@google.com (Simon Nickerson)
 */
public class OrderingFromPositiveCases {

  public static void positiveCase1() {
    // BUG: Suggestion includes "new Ordering<String>("
    Ordering<String> ord =
        Ordering.from(
            new Comparator<String>() {
              @Override
              public int compare(String first, String second) {
                int compare = first.length() - second.length();
                return (compare != 0) ? compare : first.compareTo(second);
              }
            });
  }
}
