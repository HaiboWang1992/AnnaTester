import edu.umd.cs.findbugs.annotations.ExpectWarning;
import java.io.FileInputStream;
import java.io.IOException;

// Now bug #676 OBL false positive
// https://sourceforge.net/p/findbugs/bugs/676/

public class Bug2353694 {

  /* ********************
   * Behavior at filing: OBL false positive already resolved correct OS
   * warning thrown for in2 warning thrown => M B OS_OPEN_STREAM OS:
   * Bug2353694.fp_warning_OS() may fail to close \ stream At
   * Bug2353694.java:[line 26]
   *
   * *no* OS warning thrown for in3 -- maybe false negative?
   * ********************
   */
  @ExpectWarning(value = "OS", num = 2)
  public void fp_warning_OS() throws IOException {
    FileInputStream in2 = new FileInputStream("test");
    in2.read();

    FileInputStream in = new FileInputStream("test");
    try {
      in.read();
    } finally {
      in.close();
    }

    FileInputStream in3 = new FileInputStream("test");
    in3.read();
  }

  /* ********************
   * Behavior at filing: correct OS warning thrown for in3 warning thrown =>
   * oM B OS_OPEN_STREAM OS: Bug2353694.correct_warning_OS() may fail to \
   * close stream At Bug2353694.java:[line 50] ********************
   */
  @ExpectWarning(value = "OS", num = 1)
  public void correct_warning_OS() throws IOException {
    FileInputStream in = new FileInputStream("test");
    try {
      in.read();
    } finally {
      in.close();
    }

    FileInputStream in3 = new FileInputStream("test");
    in3.read();
  }
}
