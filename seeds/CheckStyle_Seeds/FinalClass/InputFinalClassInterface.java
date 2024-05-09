/*
FinalClass


*/


public interface InputFinalClassInterface {

  final class FinalClass { // ok
    private FinalClass() {}
  }

  class DerivedClass extends SuperClass { // violation
    private DerivedClass() {}
  }

  class SuperClass { // ok
    private SuperClass() {}
  }
}
