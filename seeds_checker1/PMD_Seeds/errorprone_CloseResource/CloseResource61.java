import java.io.*;

class CloseResourceWithVar {
  public int bar() throws IOException {
    var inputStream = new FileInputStream("bar.txt");
    int c = inputStream.read();
    return c;
  }
}
