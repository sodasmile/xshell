package com.sodasmile.xshell.console;

import jline.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class JLineConsole implements Console {

    private final ConsoleReader consoleReader;

    public JLineConsole(final InputStream in, final PrintStream out, final Completor completor) {
        try {

            this.consoleReader = new ConsoleReader(in, new OutputStreamWriter(out));

            consoleReader.setBellEnabled(false);

            String prompt = "xshell> ";
            if (consoleReader.getTerminal().isANSISupported()) {
                ANSIBuffer ansi = new ANSIBuffer();
                prompt = ansi.yellow(prompt).toString();
            }

            consoleReader.setDefaultPrompt(prompt);
            consoleReader.addCompletor(completor);

        } catch (IOException e) {
            throw new RuntimeException("Unable to create JLine console.", e);
        }
    }

    public String readLine() {
        try {
            return consoleReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read console input.", e);
        }
    }

    public void writeInfo(final String message) {
        Logger.getLogger(JLineConsole.class.getName()).info(message);
    }

    public void writeDebug(final String message) {
        Logger logger = Logger.getLogger(JLineConsole.class.getName());
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(message);
        }
    }

    public void writeError(String message) {
        Logger.getLogger(JLineConsole.class.getName()).severe(message);
    }
    
}
