import lombok.EqualsAndHashCode;

@EqualsAndHashCode
class Foo {
  private String bar;

  public void set(String s) {
    bar = s;
  }
}
