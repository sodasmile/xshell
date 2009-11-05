package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;
import jline.Completor;
import jline.MultiCompletor;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class InsideDomainCompletor extends MultiCompletor implements XShellCompletor {

    public InsideDomainCompletor(final XShell xshell) {
        super(new Completor[]{
                new SimpleEnumCompletor(Keyword.quit, Keyword.help),
                new MBeanCommandCompletor(xshell)
        });
    }

}