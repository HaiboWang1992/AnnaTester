import java.util.ArrayList;
import java.util.List;

class Buzz {
  public void buzz() {
    List<String> foo,
        bar = new ArrayList<String>(), // flagged
        baz = new ArrayList<>(); // ok
  }
}
