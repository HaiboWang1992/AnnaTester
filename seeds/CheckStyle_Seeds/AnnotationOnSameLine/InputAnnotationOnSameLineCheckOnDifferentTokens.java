import java.util.List;

@Ann // violation
@Ann2
interface TestInterface {

  @Ann // violation
  @Ann2
  Integer getX();
}

public @Ann // violation
@Ann2 class InputAnnotationOnSameLineCheckOnDifferentTokens
    implements @Ann // violation
    @Ann2 TestInterface {

  @Ann // violation
  @Ann2
  private Integer x =
      new @Ann // violation
      @Ann2 Integer(0);

  private List<
          @Ann // violation
          @Ann2 Integer>
      integerList;

  @Ann // violation
  @Ann2
  enum TestEnum {
    A1,
    A2
  }

  @Ann // violation
  @Ann2
  public InputAnnotationOnSameLineCheckOnDifferentTokens() {}

  @Ann // violation
  @Ann2
  public void setX(
      @Ann // violation
          @Ann2
          int x)
      throws @Ann // violation
          @Ann2 Exception {
    this
        .<@Ann // violation
            @Ann2 Integer>
            getXAs();
    this.x = x;
  }

  @Override
  public Integer getX() {
    return (@Ann // violation
        @Ann2 Integer)
        x;
  }

  public <T> T getXAs() {
    return (T) x;
  }
}

@Ann // violation
@Ann2
@interface TestAnnotation {

  @Ann // violation
  @Ann2
  int x();
}
