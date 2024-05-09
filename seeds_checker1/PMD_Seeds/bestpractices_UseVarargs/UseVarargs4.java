import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class Foo implements InvocationHandler {
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return null;
  }
}
