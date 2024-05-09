import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bug2177967 {

  Properties properties = new Properties();

  public void init(Class loader, String propertiesFile) throws IOException {

    // Load the properties from the file
    InputStream in = loader.getResourceAsStream(propertiesFile);
    if (in == null) {
      throw new RuntimeException("Cound not locate " + propertiesFile);
    }
    properties.load(in);
  }

  public void init(String propertiesFile) throws IOException {

    // Load the properties from the file
    InputStream in = new FileInputStream(propertiesFile);
    if (in == null) {
      throw new RuntimeException("Cound not locate " + propertiesFile);
    }
    properties.load(in);
  }
}
