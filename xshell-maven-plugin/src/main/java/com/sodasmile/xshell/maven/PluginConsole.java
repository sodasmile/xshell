package com.sodasmile.xshell.maven;

import com.sodasmile.xshell.console.JLineConsole;
import jline.Completor;
import org.apache.maven.plugin.logging.Log;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class PluginConsole extends JLineConsole {

    private final Log log;

    public PluginConsole(final InputStream in, final PrintStream out, final Completor completor, final Log log) {
        super(in, out, completor);
        this.log = log;
    }

    @Override
    public void writeInfo(String message) {
        log.info(message);
    }

    public void writeDebug(String message) {
        log.debug(message);
    }

    public void writeError(String message) {
        log.error(message);
    }

}
