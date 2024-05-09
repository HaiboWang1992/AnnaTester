/*
MissingCtor


*/


public class InputMissingCtor // violation
 {}
// we shouldn't flag abstract classes
abstract class AbstractClass {}

// this class has ctor
class CorrectClass {
  CorrectClass() {}
}
