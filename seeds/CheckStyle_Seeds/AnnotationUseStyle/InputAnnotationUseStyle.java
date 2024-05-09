@interface InputAnnotationUseStyle {
  @Another32(value = {"foo", "bar"}) // expanded // ok
  DOGS[] pooches();
}

@interface Another32 {
  String[] value();
}
