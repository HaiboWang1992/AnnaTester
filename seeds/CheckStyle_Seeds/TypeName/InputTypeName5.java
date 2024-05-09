/*
TypeName
format = (default)^[A-Z][a-zA-Z0-9]*$
applyToPublic = (default)true
applyToProtected = (default)true
applyToPackage = (default)true
applyToPrivate = (default)true
tokens = ENUM_DEF


*/


class inputHeaderClass5 {

  public interface inputHeaderInterface {}
  ;
  // comment
  public enum inputHeaderEnum {
    one,
    two
  }; // violation

  public @interface inputHeaderAnnotation {};
}

public class InputTypeName5 {}
