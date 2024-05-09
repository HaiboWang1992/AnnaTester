import java.io.InputStream;

class UsedLocalVar {
  public boolean run() {
    boolean canRead = false;
    try (InputStream resource = open()) {
      canRead = true;
    } catch (Throwable ignore) {
    }
    return canRead;
  }

  private InputStream open() {
    return null;
  }
}
