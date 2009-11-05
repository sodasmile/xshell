package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

import java.util.Arrays;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class DomainCommand extends KeywordCommand {

    public DomainCommand(final String[] arguments) {
        super(Keyword.domain, arguments);
    }

    @Override
    public void execute(final XShell xshell) {

        if (arguments().length != 1) {
            throw new IllegalArgumentException("The domain command expects a single argument. You typed 'domain " + Arrays.toString(arguments()) + "'.");
        }

        xshell.domain(arguments()[0]);
    }

}
