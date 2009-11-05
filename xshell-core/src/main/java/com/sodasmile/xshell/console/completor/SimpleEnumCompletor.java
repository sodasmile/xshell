package com.sodasmile.xshell.console.completor;

import com.sodasmile.xshell.Keyword;
import jline.SimpleCompletor;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public class SimpleEnumCompletor extends SimpleCompletor {

    public SimpleEnumCompletor(final Keyword... keywords) {
        super(names(keywords));
    }

    static String[] names(final Keyword... keywords) {
        String[] names = new String[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            names[i] = keywords[i].name();
        }

        return names;
    }

}
