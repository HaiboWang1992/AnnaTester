import javax.crypto.spec.IvParameterSpec;

class Foo {

  void encrypt() {
    byte[] staticIv = "ALL_ZEROS_HERE".getBytes();
    IvParameterSpec iv = new IvParameterSpec(staticIv);
  }
}
