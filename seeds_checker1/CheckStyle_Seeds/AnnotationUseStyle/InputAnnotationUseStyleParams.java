import java.lang.annotation.Target;

public class InputAnnotationUseStyleParams {
  @Target({}) // ok
  public @interface myAnn {}
}
