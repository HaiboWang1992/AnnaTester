import javax.crypto.spec.IvParameterSpec;

class Foo {

  byte[] ivBytes =
      new byte[] {
        00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00,
      };

  void encrypt() {
    IvParameterSpec iv = new IvParameterSpec(ivBytes);
  }
}
