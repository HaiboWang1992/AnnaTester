import edu.umd.cs.findbugs.annotations.Confidence;
import edu.umd.cs.findbugs.annotations.DesireWarning;
import edu.umd.cs.findbugs.annotations.ExpectWarning;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author pugh
 */
public class Bug3104124 {

  @DesireWarning(value = "OS_OPEN_STREAM_EXCEPTION_PATH", confidence = Confidence.MEDIUM)
  @ExpectWarning(value = "OS_OPEN_STREAM_EXCEPTION_PATH", confidence = Confidence.LOW)
  public static String fileToString(String fileName) {
    StringBuffer output = new StringBuffer();
    try {
      String line;
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      while ((line = br.readLine()) != null) {
        output.append(line).append("\n");
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return output.toString();
  }
}
