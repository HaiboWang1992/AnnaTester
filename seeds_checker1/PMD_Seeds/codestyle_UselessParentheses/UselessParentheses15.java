import java.io.IOException;

class NewClass {

  public void falsePositive(Boolean b) throws IOException {
    System.out.write(("" + b).getBytes());
  }
}
