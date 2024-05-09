import java.io.ObjectStreamField;

public class InputConstantName // ok
 {
  private static final long serialVersionUID = 1L; // should be ignored
  private static final ObjectStreamField[] serialPersistentFields = {}; // should be ignored too
  static int value1 = 10;
  final int value2 = 10;
}
