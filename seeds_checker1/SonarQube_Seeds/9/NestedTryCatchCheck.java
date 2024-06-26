import java.io.Closeable;
import java.io.IOException;

class NestedTryCatchCheck {
  public void f() throws Exception {
    try {
    } finally {
    }

    try {
      try { // Compliant
      } finally {
      }
    } catch (Exception e) {
    }

    try {
    } catch (Exception e) {
      try { // Compliant
      } catch (Exception e1) {
      }
    }

    try {
    } catch (Exception e) {
      try { // Compliant
      } finally {
      }
    }

    try {
    } catch (Exception e) {
      try { // Compliant
      } catch (Exception e1) {
      }
    } finally {
      try { // Compliant
      } catch (Exception e) {
      }
    }

    try {
      try { // Noncompliant {{Extract this nested try block into a separate method.}}
      } catch (Exception e) {
      }

      try { // Noncompliant
      } catch (Exception e) {
        try { // Noncompliant [[sc=9;ec=12;secondary=44]]

        } catch (Exception e1) {
        }
      }
    } catch (Exception e) {
    }

    try (Resource r = new Resource()) { // Compliant
      try (Resource r2 = new Resource()) { // Compliant
        try { // Compliant
        } finally {
        }
      }
    }

    try {
      try { // Compliant
      } catch (Exception e) {
      }
    } finally {
    }

    try {
      try { // Noncompliant
        try (Resource r = new Resource()) {}
      } catch (Exception e) {
      }
    } catch (Exception e) {
    }
  }

  private static class Resource implements Closeable {
    @Override
    public void close() throws IOException {}
  }
}

class AnonymousClass {

  static {
    try {
      try { // Noncompliant {{Extract this nested try block into a separate method.}}
      } catch (Exception e) {
      }
    } catch (Exception e) {
    }
  }

  void foo() {
    try {
      new AnonymousClass() {

        {
          try {
            try { // Noncompliant {{Extract this nested try block into a separate method.}}
            } catch (Exception e) {
            }
          } catch (Exception e) {
          }
        }

        {
          try { // compliant - not included in count of parent method
          } catch (Exception e) {
          }
        }

        @Override
        void foo() {
          try { // Compliant - not included in count of parent method
          } catch (Exception e) {
          }
        }

        @Override
        void bar() {
          try {
            try { // Noncompliant {{Extract this nested try block into a separate method.}}
            } catch (Exception e) {
            }
          } catch (Exception e) {
          }
        }
      };
    } catch (Exception e) {
    }
  }

  void bar() {}
}

abstract class Lambda {

  String foo(java.util.Map<String, String> myMap, String key) throws MyException {
    try {
      return myMap.computeIfAbsent(
          key,
          k -> {
            try { // Compliant - within body of lambda
              return getValue(key);
            } catch (Exception e) {
              throw new MyRuntimeException();
            }
          });
    } catch (MyRuntimeException e) {
      throw new MyException();
    }
  }

  String bar(java.util.Map<String, String> myMap, String key) {
    return myMap.computeIfAbsent(
        key,
        k -> {
          try {
            try { // Noncompliant {{Extract this nested try block into a separate method.}}
            } catch (Exception e) {
            }
          } catch (Exception e) {
          }
          return k;
        });
  }

  abstract String getValue(String s) throws Exception;

  private static class MyRuntimeException extends RuntimeException {}

  private static class MyException extends Exception {}
}
