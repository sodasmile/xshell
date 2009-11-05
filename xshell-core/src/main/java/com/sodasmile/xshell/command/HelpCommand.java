package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class HelpCommand extends KeywordCommand {

    public HelpCommand(final String[] arguments) {
        super(Keyword.help, arguments);
    }

    @Override
    public void execute(XShell xshell) {
        System.err.println("HELP COMMAND");
    }

}
