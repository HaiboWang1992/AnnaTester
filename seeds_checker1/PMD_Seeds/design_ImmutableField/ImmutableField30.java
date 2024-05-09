import java.math.BigInteger;
import java.util.Random;

class TestFinal {
  private BigInteger e;

  public TestFinal() {
    Random random = new Random();
    e = BigInteger.probablePrime(Integer.MAX_VALUE / 2, random);

    while (random.nextBoolean()) e = e.add(BigInteger.ONE);
  }
}
