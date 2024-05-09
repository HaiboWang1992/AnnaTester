import com.puppycrawl.tools.checkstyle.*;
import java.net.*;
import java.util.*;
import org.antlr.v4.runtime.*;

public class InputIllegalTypeTestStarImports {
  List<Integer> l = new LinkedList<>(); // violation
}
