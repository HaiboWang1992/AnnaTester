import java.io.RandomAccessFile;

class FalsePositive {
  private boolean isHundredBytes = false;

  public void bar() throws Exception {
    RandomAccessFile file = null;
    try {
      file = new RandomAccessFile("name", "r");
      this.isHundredBytes = file.length() == 100; /* violation here... */
    } finally {
      file.close();
    }
  }
}
