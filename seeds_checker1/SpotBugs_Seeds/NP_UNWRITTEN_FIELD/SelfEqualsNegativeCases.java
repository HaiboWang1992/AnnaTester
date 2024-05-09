import com.google.common.base.Objects;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SelfEqualsNegativeCases {
  private String field;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SelfEqualsNegativeCases other = (SelfEqualsNegativeCases) o;
    return Objects.equal(field, other.field);
  }

  @Override
  public int hashCode() {
    return field != null ? field.hashCode() : 0;
  }

  public boolean equals2(Object o) {
    if (!(o instanceof SelfEqualsNegativeCases)) {
      return false;
    }

    SelfEqualsNegativeCases other = (SelfEqualsNegativeCases) o;
    return field.equals(other.field);
  }

  public boolean test() {
    return Boolean.TRUE.toString().equals(Boolean.FALSE.toString());
  }
}
