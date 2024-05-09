import com.google.common.base.Objects;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SelfEqualsPositiveCase1 {
  private String field = "";

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SelfEqualsPositiveCase1 other = (SelfEqualsPositiveCase1) o;
    boolean retVal;
    // BUG: Suggestion includes "Objects.equal(field, other.field)"
    retVal = Objects.equal(field, field);
    // BUG: Suggestion includes "Objects.equal(other.field, this.field)"
    retVal &= Objects.equal(field, this.field);
    // BUG: Suggestion includes "Objects.equal(this.field, other.field)"
    retVal &= Objects.equal(this.field, field);
    // BUG: Suggestion includes "Objects.equal(this.field, other.field)"
    retVal &= Objects.equal(this.field, this.field);

    return retVal;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(field);
  }

  public static void test() {
    ForTesting tester = new ForTesting();
    // BUG: Suggestion includes "Objects.equal(tester.testing.testing, tester.testing)"
    Objects.equal(tester.testing.testing, tester.testing.testing);
  }

  private static class ForTesting {
    public ForTesting testing;
    public String string;
  }
}
