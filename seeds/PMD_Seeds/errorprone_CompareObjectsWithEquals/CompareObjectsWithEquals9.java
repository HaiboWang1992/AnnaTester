
import java.io.File;

public class CompareObjectsWithEqualsSample {
  boolean bar(String b) {
    return new File(b).exists() == false;
  }
}
