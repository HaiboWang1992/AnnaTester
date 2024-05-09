import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
class Foo {
  public static Foo get() {
    return null;
  }
}
