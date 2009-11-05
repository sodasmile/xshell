package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class ConnectCommand extends KeywordCommand {

    public ConnectCommand(final String[] arguments) {
        super(Keyword.connect, arguments);
    }

    @Override
    public void execute(final XShell xshell) {
        xshell.connect();
    }

}
