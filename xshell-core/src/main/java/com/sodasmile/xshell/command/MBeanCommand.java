package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class MBeanCommand extends KeywordCommand {

    public MBeanCommand(final String[] arguments) {
        super(Keyword.mbean, arguments);
    }

    @Override
    public void execute(final XShell xshell) {
        xshell.mbean(arguments()[0]);
    }

}
