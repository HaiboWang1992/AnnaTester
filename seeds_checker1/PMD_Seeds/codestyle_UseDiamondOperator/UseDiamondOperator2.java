import java.util.ArrayList;

class Buzz {
  public void buzz() {
    var f = new ArrayList<String>(); // ok
    f = new ArrayList<>(); // ok
    f = new ArrayList<String>(); // flagged by rule
  }
}
