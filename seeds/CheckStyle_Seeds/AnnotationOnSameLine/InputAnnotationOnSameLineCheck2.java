import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class InputAnnotationOnSameLineCheck2 {

  @Ann // violation
  private List<String> names = new ArrayList<>();

  @Ann private List<String> names2 = new ArrayList<>(); // ok

  @SuppressWarnings("deprecation") // violation
  @Ann
  Integer x;

  @SuppressWarnings("deprecation") // violation
  @Ann // violation
  Integer x2;

  @SuppressWarnings("deprecation")
  @Ann
  @Ann2
  @Ann3
  @Ann4
  Integer x3; // ok
}

@Target({CONSTRUCTOR, FIELD, METHOD, PARAMETER, TYPE, TYPE_PARAMETER, TYPE_USE})
@interface Ann {}

@Target({CONSTRUCTOR, FIELD, METHOD, PARAMETER, TYPE, TYPE_PARAMETER, TYPE_USE})
@interface Ann2 {}

@interface Ann3 {}

@interface Ann4 {}
