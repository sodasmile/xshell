package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class DisconnectedCompletor extends SimpleEnumCompletor implements XShellCompletor {

    public DisconnectedCompletor(final XShell xshell) {
        super(Keyword.connect, Keyword.quit, Keyword.help);
    }

}
