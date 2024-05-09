import java.util.ArrayList;
import java.util.Date;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CollectionIncompatibleTypeNegativeCases {

  public boolean ok1() {
    return new ArrayList<String>().contains("ok");
  }

  class B extends Date {}

  public boolean ok2() {
    return new ArrayList<Date>().contains(new B());
  }

  public boolean ok3() {
    return new OtherCollection<String>().contains(new B());
  }

  private class OtherCollection<E> {
    public boolean contains(Object o) {
      return true;
    }
  }
}
