import javax.annotation.Nullable;

public class PackageLevel {
  public String method() {
    return methodNullable(null);
  }

  private String methodNullable(@Nullable final String test) {
    return methodNonNull(test);
  }

  private String methodNonNull(final String test) {
    return test;
  }
}
