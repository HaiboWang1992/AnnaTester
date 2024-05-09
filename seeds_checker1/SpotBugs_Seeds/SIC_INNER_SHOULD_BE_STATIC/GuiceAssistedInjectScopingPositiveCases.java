import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.servlet.RequestScoped;

/**
 * @author eaftan@google.com (Eddie Aftandilian)
 */
public class GuiceAssistedInjectScopingPositiveCases {

  // BUG: Suggestion includes "remove this line"
  @Singleton
  public class TestClass {
    @Inject
    public TestClass(String unassisted, @Assisted String assisted) {}
  }

  // BUG: Suggestion includes "remove this line"
  @RequestScoped
  public class TestClass2 {
    @Inject
    public TestClass2(String unassisted, @Assisted String assisted) {}
  }

  // BUG: Suggestion includes "remove this line"
  @Singleton
  public class TestClass3 {
    @AssistedInject
    public TestClass3(String param) {}
  }

  /** Multiple constructors, but only one with @Inject, and that one matches. */
  // BUG: Suggestion includes "remove this line"
  @Singleton
  public class TestClass4 {
    @Inject
    public TestClass4(String unassisted, @Assisted String assisted) {}

    public TestClass4(String unassisted, int i) {}

    public TestClass4(int i, String unassisted) {}
  }

  /** Multiple constructors, none with @Inject, one matches. */
  // BUG: Suggestion includes "remove this line"
  @Singleton
  public class TestClass5 {
    public TestClass5(String unassisted1, String unassisted2) {}

    public TestClass5(String unassisted, int i) {}

    @AssistedInject
    public TestClass5(int i, String unassisted) {}
  }

  /** JSR330 annotations. */
  // BUG: Suggestion includes "remove this line"
  @javax.inject.Singleton
  public class TestClass6 {
    @javax.inject.Inject
    public TestClass6(String unassisted, @Assisted String assisted) {}
  }
}
