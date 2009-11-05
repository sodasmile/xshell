package com.sodasmile.xshell.command;

import com.sodasmile.xshell.XShell;

import java.io.Serializable;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public abstract class Command implements Serializable {

    private final String[] arguments;

    public Command(final String[] arguments) {
        this.arguments = arguments;
    }

    public String[] arguments() {
        return arguments;
    }

    public abstract void execute(final XShell xshell);

}
