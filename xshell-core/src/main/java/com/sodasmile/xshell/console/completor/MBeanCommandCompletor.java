package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;
import jline.ArgumentCompletor;
import jline.Completor;
import jline.NullCompletor;
import jline.SimpleCompletor;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class MBeanCommandCompletor extends ArgumentCompletor implements XShellCompletor {
    public MBeanCommandCompletor(final XShell xshell) {
        super(new Completor[]{new SimpleEnumCompletor(Keyword.mbean), new SimpleCompletor(xshell.mbeans()), new NullCompletor()});
    }
}
