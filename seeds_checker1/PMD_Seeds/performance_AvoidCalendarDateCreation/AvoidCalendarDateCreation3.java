import java.util.Calendar;

class Foo {
  void foo() {
    long time1 = 0;
    Calendar cal = Calendar.getInstance();
    long time2 = cal.getTimeInMillis();
    time1 = cal.getTimeInMillis();
  }
}
