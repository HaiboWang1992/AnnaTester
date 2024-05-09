import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CollectionIncompatibleTypePositiveCases {
  Collection<String> collection = new ArrayList<String>();

  public boolean bug() {
    // BUG: Suggestion includes "return false"
    return collection.contains(this);
  }

  public boolean bug2() {
    // BUG: Suggestion includes "return false"
    return new ArrayList<String>().remove(new Date());
  }

  public boolean bug3() {
    List<String> list = new ArrayList<String>(collection);
    // BUG: Suggestion includes "return false"
    return list.contains(new Exception());
  }

  public String bug4() {
    Map<Integer, String> map = new HashMap<Integer, String>();
    // BUG: Suggestion includes "return false"
    return map.get("not an integer");
  }
}
