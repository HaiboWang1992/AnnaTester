import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class Foo {
  public static void charset() {
    // looking up the charset dynamically
    try (OutputStreamWriter osw = new OutputStreamWriter(out, Charset.forName("US-ASCII"))) {
      osw.write("test");
    }
  }
}
