import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;

public class PreconditionsCheckNotNullPositiveCase1 {
  public void error() {
    // BUG: Suggestion includes "remove this line"
    Preconditions.checkNotNull("string literal");
    String thing = null;
    // BUG: Suggestion includes "(thing, "
    checkNotNull("thing is null", thing);
    // BUG: Suggestion includes ""
    Preconditions.checkNotNull("a string literal " + "that's got two parts", thing);
  }
}
