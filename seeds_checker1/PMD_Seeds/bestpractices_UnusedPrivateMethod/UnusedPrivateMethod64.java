class Outer {
  private void outerUnusedMethod() {}

  class Inner {
    private void innerUnusedMethod() {} // false negative

    private void innerUsedByInnerMethod() {}

    public void publicInnerMethod() {
      innerUsedByInnerMethod();
    }

    private void innerUsedByOuterMethod() {}
  }

  public void publicOuterMethod() {
    Inner inner = new Inner();
    inner.innerUsedByOuterMethod();
  }
}
