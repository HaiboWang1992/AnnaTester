import java.io.File;

class CompareObjectsWithEqualsSample {
  boolean bar(String b) {
    return new File(b).exists() == false;
  }
}
