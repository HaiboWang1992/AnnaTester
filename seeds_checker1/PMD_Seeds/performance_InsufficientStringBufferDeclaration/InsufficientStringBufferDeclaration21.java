import java.util.List;

class Foo {
  public void bar(List l) {
    StringBuffer sb = new StringBuffer();
    if (true) {
      sb.append("1234567890");
    } else if (l.size() == 5) {
      sb.append("1234567890");
    } else {
      sb.append("1234567890");
    }
  }

  public void bar2(List l) {
    StringBuilder sb = new StringBuilder();
    if (true) {
      sb.append("1234567890");
    } else if (l.size() == 5) {
      sb.append("1234567890");
    } else {
      sb.append("1234567890");
    }
  }
}
