import lombok.experimental.Delegate;

class Foo {
  @Delegate private String x;

  public Foo() {
    x = "bar";
  }
}
