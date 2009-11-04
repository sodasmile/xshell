package com.sodasmile.xshell.console;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public interface Console {

    String readLine();

    void writeInfo(String message);

    void writeDebug(String message);

    void writeError(String message);

}