import java.lang.annotation.Repeatable;

class InputAnnotationLocationParameterized {

  @Annotation
  void singleParameterless() {} // violation

  @Annotation
  @Annotation
  void multipleParameterless() {} // 2 violations

  @Annotation("")
  void parameterized() {}

  @Annotation(value = "")
  void namedParameterized() {}

  @Annotation
  @Annotation("")
  @Annotation(value = "")
  void multiple() {} // 3 violations

  @Annotation("")
  @Annotation(value = "")
  void multipleParametrized() {} // violation

  @Repeatable(Annotations.class)
  @interface Annotation {
    String value() default "";
  }

  @interface Annotations {
    Annotation[] value();
  }
}
