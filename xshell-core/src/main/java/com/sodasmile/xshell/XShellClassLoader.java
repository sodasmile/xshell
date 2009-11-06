package com.sodasmile.xshell;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class XShellClassLoader extends URLClassLoader {

    private static Logger log = Logger.getLogger(XShellClassLoader.class.getName());

    public XShellClassLoader(final String[] classpath, final ClassLoader parent) {
        super(toUrlArray(classpath), parent);
    }

    private static URL[] toUrlArray(final String[] classpath) {

        URL[] urls = new URL[classpath.length];

        for (int i = 0; i < classpath.length; i++) {
            String entry = classpath[i];
            try {

                File file = new File(entry);
                if (!file.exists()) {
                    log.warning("Classpath entry '" + entry + "' does not exist.");
                }

                urls[i] = file.toURI().toURL();

            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Unable to convert classpath entry '" + entry + "' to an URL.", e);
            }
        }

        return urls;
    }

}
