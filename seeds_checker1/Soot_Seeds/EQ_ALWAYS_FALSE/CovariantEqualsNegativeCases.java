import java.util.List;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CovariantEqualsNegativeCases {
  @Override
  public boolean equals(Object obj) {
    return false;
  }

  public boolean equals(CovariantEqualsNegativeCases other, String s) {
    return false;
  }

  public void equals(CovariantEqualsNegativeCases other) {}

  public List<Integer> equals(Integer other) {
    return null;
  }
}

class CovariantEqualsNegativeCase2 {
  @SuppressWarnings("CovariantEquals")
  public boolean equals(CovariantEqualsNegativeCase2 other) {
    return false;
  }
}

class AnotherClass {
  public boolean equals(CovariantEqualsNegativeCases other) {
    return false;
  }

  public int[] equals(int other) {
    return null;
  }
}

/**
 * Don't issue error when a class already overrides the real equals. In this case covariant equals
 * is probably a helper.
 */
class ClassWithEqualsOverridden {
  @Override
  public boolean equals(Object other) {
    if (other instanceof ClassWithEqualsOverridden) {
      return equals((ClassWithEqualsOverridden) other);
    } else {
      return false;
    }
  }

  public boolean equals(ClassWithEqualsOverridden other) {
    return true;
  }
}

/**
 * Don't issue error when the covariant equals method is not public. In that case it wasn't intended
 * to override equals.
 */
class ClassWithNonPublicCovariantEquals {
  boolean equals(ClassWithNonPublicCovariantEquals other) {
    return true;
  }
}
