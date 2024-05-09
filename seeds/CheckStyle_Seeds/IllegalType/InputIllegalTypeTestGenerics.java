import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public abstract class InputIllegalTypeTestGenerics {

  private Set<Boolean> privateSet; // OK
  private java.util.List<Map<Boolean, Foo>> privateList; // OK
  public Set<Boolean> set; // violation
  public java.util.List<Map<Boolean, Foo>> list; // 2 violations

  private void methodCall() {
    Bounded.<Boolean>foo(); // violation
    final Consumer<Foo> consumer = Foo<Boolean>::foo; // 2 violations
  }

  public <T extends Boolean, U extends Serializable> void typeParameter(T a) {} // 2 violations

  public void fullName(java.util.ArrayList<? super Boolean> a) {} // violation

  public abstract Set<Boolean> shortName(Set<? super Set<Boolean>> a); // 2 violations

  public Set<? extends Foo<Boolean>> typeArgument() { // 2 violations
    return new TreeSet<Foo<Boolean>>(); // OK
  }

  public class MyClass<Foo extends Boolean> {} // 2 violations
}

class Bounded {

  public boolean match = new TreeSet<Integer>().stream().allMatch(new TreeSet<>()::add); // OK

  public static <Boolean> void foo() {} // violation
}

class Foo<T extends Boolean & Serializable> { // OK

  void foo() {}
}

@interface Annotation {

  Class<? extends Boolean>[] nonPublic(); // OK

  public Class<? extends Boolean>[] value(); // violation
}
