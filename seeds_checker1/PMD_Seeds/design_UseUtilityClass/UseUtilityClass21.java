import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Foo {
  public static Foo get() {
    return null;
  }
}
