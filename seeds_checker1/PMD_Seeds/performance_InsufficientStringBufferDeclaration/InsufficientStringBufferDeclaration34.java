class Foo {
  public void bar() {
    StringBuffer sb = new StringBuffer();
    sb.append((String) null);
  }

  public void bar2() {
    StringBuilder sb = new StringBuilder();
    sb.append((String) null);
  }
}
