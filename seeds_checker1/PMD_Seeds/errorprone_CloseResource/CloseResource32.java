import java.io.*;

class CloseResourceTest {
  public static void main(String[] args) {
    InputStream in = null;
    try {
      in = new FileInputStream(new File("/tmp/foo"));
      in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
