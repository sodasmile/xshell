package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class QuitCommand extends KeywordCommand {

    public QuitCommand(final String[] arguments) {
        super(Keyword.quit, arguments);
    }

    @Override
    public void execute(XShell xshell) {
        System.err.println("QUIT COMMAND");
    }

}
