/*
HideUtilityClassConstructor


*/


/**
 * Input for HideUtilityClassConstructorCheck, a non utility class that has
 *
 * @author lkuehne
 */
public class InputHideUtilityClassConstructorRegression // ok
 {
  public long constructionTime = System.currentTimeMillis();

  public static InputHideUtilityClassConstructorRegression create() {
    return new InputHideUtilityClassConstructorRegression();
  }
}
