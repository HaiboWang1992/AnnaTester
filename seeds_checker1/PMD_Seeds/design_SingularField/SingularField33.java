import lombok.Data;

class Outer {
  @Data
  class Inner {
    private String innerField;
  }

  private String outerField;

  public Outer(String outerField) {
    this.outerField = outerField;
  }
}
