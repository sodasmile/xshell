package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class InvokeOperationCommand extends KeywordCommand {

    public InvokeOperationCommand(final String[] arguments) {
        super(Keyword.NULL, arguments);
    }

    @Override
    public void execute(final XShell xshell) {

        // TODO runebjo: Piggybacking non-keyword commands here.

        if (arguments().length > 1) {

            String[] args = new String[arguments().length - 1];
            System.arraycopy(arguments(), 1, args, 0, arguments().length - 1);

            xshell.invokeOperation(arguments()[0], args);
        } else {
            xshell.invokeOperation(arguments()[0], new String[]{});
        }

    }

}
