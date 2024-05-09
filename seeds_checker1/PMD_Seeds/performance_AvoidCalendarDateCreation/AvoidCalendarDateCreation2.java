import java.util.Calendar;

class Foo {
  void foo() {
    long time = Calendar.getInstance().getTimeInMillis();
    String timeStr = Long.toString(Calendar.getInstance().getTimeInMillis());
  }
}
