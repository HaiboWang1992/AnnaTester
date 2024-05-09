
public class AnonClassExample {
  static {
    new Thread(
            new Runnable() {
              // missing
              public void run() {}
            })
        .start();
  }
}
