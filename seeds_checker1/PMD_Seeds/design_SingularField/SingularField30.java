import lombok.Data;

@Data
class MyClass {

  private String field1;

  public MyClass(String field1) {
    this.field1 = field1;
  }
}
