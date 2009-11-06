package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;
import jline.*;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class DomainCommandCompletor extends ArgumentCompletor implements XShellCompletor {

    public DomainCommandCompletor(final XShell xshell) {
        super(new Completor[]{new SimpleEnumCompletor(Keyword.domain), new SimpleCompletor(xshell.domains()), new NullCompletor()});
    }

}
