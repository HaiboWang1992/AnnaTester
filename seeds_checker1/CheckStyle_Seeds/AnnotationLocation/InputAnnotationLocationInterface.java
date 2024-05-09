import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;

@InterfaceAnnotation(value = "foo")
@InterfaceAnnotation // violation
@InterfaceAnnotation("bar")
interface InputAnnotationLocationInterface { // violation

  @InterfaceAnnotation(value = "foo")
  @InterfaceAnnotation // violation
  @InterfaceAnnotation("bar")
  void method( // violation
      int param1,
      @InterfaceAnnotation(value = "foo") @InterfaceAnnotation @InterfaceAnnotation("bar")
          int param2,
      int param3);
}

@Repeatable(InterfaceAnnotations.class)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@interface InterfaceAnnotation {

  String value() default "";
}

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@interface InterfaceAnnotations {

  InterfaceAnnotation[] value();
}
