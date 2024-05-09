import org.junit.Before;

abstract class InputAbbreviationAsWordInNameOverridableMethod2 extends Class1 {
  public int serialNUMBER = 6; // violation
  public final int s1erialNUMBER = 6;
  private static int s2erialNUMBER = 6;
  private static final int s3erialNUMBER = 6;

  @Override
  @SuppressWarnings(value = {""})
  @Before
  protected void oveRRRRRrriddenMethod() { // violation
    int a = 0;
    // blah-blah
  }
}

class Class12 {
  @SuppressWarnings(value = {""})
  protected void oveRRRRRrriddenMethod() { // violation
    int a = 0;
    // blah-blah
  }
}

class Class22 extends Class1 {

  @Override
  @SuppressWarnings(value = {""})
  @Before
  protected void oveRRRRRrriddenMethod() { // violation
    int a = 0;
    // blah-blah
  }
}
