class Count {
  public static void main(String[] args) {
    final int START = 2000000000;
    int count = 0;
    for (float f = START; f < START + 50; f++) count++;
    // Prints 0 because (float) START == (float) (START + 50).
    System.out.println(count);
    // The termination test misbehaves due to floating point granularity.
  }
}
