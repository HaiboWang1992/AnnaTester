import java.util.List;

class TestPrivate {
  protected <T extends List> Object getProtected(final T bean) {
    return getPrivate(bean);
  }

  private Object getPrivate(final List bean) {
    return bean;
  }
}
