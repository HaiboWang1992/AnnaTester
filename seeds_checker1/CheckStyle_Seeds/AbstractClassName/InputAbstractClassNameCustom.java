/*
AbstractClassName
format = ^NonAbstract.+$
ignoreModifier = true
ignoreName = (default)false


*/


public abstract class InputAbstractClassNameCustom { // violation
}

abstract class NonAbstractClassNameCustom { // ok
}

abstract class AbstractClassOtherCustom { // violation
  abstract class NonAbstractInnerClass { // ok
  }
}

class NonAbstractClassCustom { // ok
}

class AbstractClassCustom {}

abstract class AbstractClassName2Custom { // violation
  class AbstractInnerClass {}
}
