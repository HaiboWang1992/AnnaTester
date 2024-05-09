import lombok.Data;

@Data
class Outer {
  class Inner {
    private String innerField;
  }

  private String outerField;

  public Outer(String outerField) {
    this.outerField = outerField;
  }
}
