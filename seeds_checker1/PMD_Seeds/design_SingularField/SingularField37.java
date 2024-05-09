import lombok.experimental.Delegate;

class Foo {
  @Delegate private String bar;

  public void set(String s) {
    bar = s;
  }
}
