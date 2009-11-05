package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import com.sodasmile.xshell.XShell;
import jline.Completor;
import jline.MultiCompletor;
import jline.SimpleCompletor;

import javax.management.MBeanOperationInfo;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class InsideMBeanCompletor extends MultiCompletor implements XShellCompletor {

    public InsideMBeanCompletor(final XShell xshell) {
        super(new Completor[]{
                new SimpleEnumCompletor(Keyword.quit, Keyword.help),
                new SimpleCompletor(names(xshell.operations()))
        });
    }

    private static String[] names(final MBeanOperationInfo[] info) {

        String[] names = new String[info.length];

        for (int i = 0; i < info.length; i++) {
            names[i] = info[i].getName();
        }

        return names;
    }

}