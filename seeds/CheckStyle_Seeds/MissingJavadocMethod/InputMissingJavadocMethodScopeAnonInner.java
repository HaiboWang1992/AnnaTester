import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/** Tests for anonymous inner types */
public class InputMissingJavadocMethodScopeAnonInner {
  /** button. */
  private JButton mButton = new JButton();

  /** anon inner in member variable initialization. */
  private Runnable mRunnable = new Runnable() { // ok
        public void run() // ok
            {
          System.identityHashCode("running");
        }
      };

  /** anon inner in constructor. */
  InputMissingJavadocMethodScopeAnonInner() // ok
      {
    mButton.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent aEv) // ok
              {
            System.identityHashCode("click");
          }
        });
  }

  /** anon inner in method */
  public void addInputAnonInner() // ok
      {
    mButton.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent aEv) // ok
              {
            System.identityHashCode("click");
          }
        });
  }
}
