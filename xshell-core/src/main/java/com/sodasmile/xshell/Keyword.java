package com.sodasmile.xshell;

import com.sodasmile.xshell.command.*;

import java.lang.reflect.Constructor;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public enum Keyword {
    
    domain(DomainCommand.class),
    mbean(MBeanCommand.class),
    connect(ConnectCommand.class),
    quit(QuitCommand.class),
    help(HelpCommand.class),
    NULL(NonKeywordCommand.class);

    private final Class<? extends KeywordCommand> commandClass;

    Keyword(Class<? extends KeywordCommand> commandClass) {
        this.commandClass = commandClass;
    }

    public static boolean isKeyword(final String name) {
        for (Keyword k : Keyword.values()) {
            if (k.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public KeywordCommand command(final String[] arguments) {

        try {

            Constructor<? extends KeywordCommand> constructor = commandClass.getConstructor(String[].class);

            return constructor.newInstance(new Object[]{arguments});

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("KeywordCommand class '" + commandClass + "' does not provide a single String-array argument constructor.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an instance of KeywordCommand class '" + commandClass + "'.", e);
        }

    }

}
