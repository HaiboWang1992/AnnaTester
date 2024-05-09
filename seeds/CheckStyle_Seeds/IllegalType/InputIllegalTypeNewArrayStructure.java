import java.util.HashMap;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class InputIllegalTypeNewArrayStructure {
  void method(int x) {
    int numberOfTests = x + 9;
    if (x > 7) {
      HashMap<String, KeyValue>[] kvMaps = new HashMap[numberOfTests]; // violation
    }
  }
}
