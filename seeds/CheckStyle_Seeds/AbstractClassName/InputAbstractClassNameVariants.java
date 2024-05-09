/*
AbstractClassName
format = (default)^Abstract.+$
ignoreModifier = (default)false
ignoreName = (default)false


*/


public abstract class InputAbstractClassNameVariants { // violation
}

abstract class NonAbstractClassNameVa { // violation
}

abstract class AbstractClassOtherVa { // ok
  abstract class NonAbstractInnerClassVa { // violation
  }
}

class NonAbstractClassVa {}

class AbstractClassVa { // violation
}

abstract class AbstractClassName2Va { // ok
  class AbstractInnerClassVa { // violation
  }
}
