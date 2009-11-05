package com.sodasmile.xshell.console;

import jline.ConsoleReader;

/**
 * TODO runepeter: No need for this interface. Just merge with the impl.
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public interface Console {

    ConsoleReader consoleReader();

    String readLine();

    void writeInfo(String message);

    void writeDebug(String message);

    void writeError(String message);

    void setPrompt(String message);
}