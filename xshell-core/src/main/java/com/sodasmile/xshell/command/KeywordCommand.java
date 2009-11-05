package com.sodasmile.xshell.command;

import com.sodasmile.xshell.Keyword;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public abstract class KeywordCommand extends Command {

    private final Keyword keyword;

    public KeywordCommand(final Keyword keyword) {
        super(new String[]{});
        this.keyword = keyword;
    }

    public KeywordCommand(final Keyword keyword, final String[] arguments) {
        super(arguments);
        this.keyword = keyword;
    }

    public Keyword keyword() {
        return keyword;
    }

}
