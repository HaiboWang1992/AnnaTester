import java.net.URL;

public class InputRedundantImportBug { // ok
  // same as a class name
  private static String URL = "This is a String object";

  public InputRedundantImportBug() throws Exception {
    URL url = new URL("file://this.is.a.url.object");
  }
}
