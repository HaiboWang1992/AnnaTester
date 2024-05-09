import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;

public class InputOneStatementPerLineTryWithResourcesIgnore {

  void method() throws IOException {
    OutputStream s1 = new PipedOutputStream();
    OutputStream s2 = new PipedOutputStream();
    try (s1;
        s2;
        OutputStream s3 = new PipedOutputStream(); ) { // ok
    }
    try (s1;
        OutputStream s4 = new PipedOutputStream();
        s2; ) { // ok
    }
    try (s1;
        s2;
        OutputStream s5 = new PipedOutputStream()) { // ok
    }
    try (s1;
        OutputStream s6 = new PipedOutputStream();
        s2) { // ok
    }
    try (OutputStream s7 = new PipedOutputStream();
        OutputStream s8 = new PipedOutputStream();
        s2; ) {}
    try (OutputStream s9 = new PipedOutputStream();
        s2;
        OutputStream s10 = new PipedOutputStream()) {}
    try (s1;
        OutputStream s11 = new PipedOutputStream();
        s2; ) {}
    try (OutputStream s12 = new PipedOutputStream();
        s1;
        OutputStream s3 = new PipedOutputStream();
        s2; ) {}
    try (OutputStream s12 = new PipedOutputStream();
        s1;
        OutputStream s3 // ok
            = new PipedOutputStream()) {}
    try (s1;
        s2;
        OutputStream stream3 = new PipedOutputStream()) {}
    try (OutputStream s10 = new PipedOutputStream();
        OutputStream s11 = new PipedOutputStream();
        s2; ) {}
  }
}
