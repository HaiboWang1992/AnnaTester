import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Test case for static import of Precondtions.checkNotNull.
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class PreconditionsCheckNotNullPositiveCase3 {
  public void error() {
    // BUG: Suggestion includes "remove this line"
    checkNotNull("string literal");
  }
}
