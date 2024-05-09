/*
EmptyBlock
option = (default)STATEMENT
tokens = LITERAL_DEFAULT


*/


public @interface InputEmptyBlockAnnotationDefaultKeyword {
  String name() default ""; // ok
}
