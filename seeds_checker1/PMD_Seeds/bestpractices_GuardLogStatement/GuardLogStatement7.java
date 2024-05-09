import java.util.logging.Logger;

class Foo {

  private void foo(Logger logger) {
    logger.fine("debug message: " + this);
  }
}
