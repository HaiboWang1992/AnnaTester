
public interface CloneableInterfaceOverride extends CloneableInterface {

  // Missing @Override
  CloneableInterface clone() throws CloneNotSupportedException;
}
