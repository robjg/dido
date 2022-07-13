package dido.operators;

import org.oddjob.Main;

/**
 * Eclipse puts test classes on the classpath, Intellij doesn't. The allows us to run Oddjob with the test
 * classpath from intellij.
 */
public class MainForIntellij {

    public static void main(String... args) throws Exception {
        Main.main(args);
    }
}
