package com.sodasmile.xshell;

import com.sodasmile.xshell.console.completor.*;

import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public enum State {
    /**
     * Not connected to the JmxConnectionProvider.
     */
    DISCONNECTED(DisconnectedCompletor.class),

    /**
     * Connected to the JmxConnectionProvider.
     */
    CONNECTED(ConnectedCompletor.class),

    /**
     * Context inside a domain.
     */
    INSIDE_DOMAIN(InsideDomainCompletor.class),

    /**
     * Context inside a specific MBean.
     */
    INSIDE_MBEAN(InsideMBeanCompletor.class);

    private final Class<? extends XShellCompletor> completorClass;

    State(Class<? extends XShellCompletor> completorClass) {
        this.completorClass = completorClass;
    }

    public XShellCompletor create(final XShell xshell) {

        try {

            Constructor<? extends XShellCompletor> constructor = completorClass.getConstructor(XShell.class);
            return constructor.newInstance(xshell);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("XShellCompletor class '" + completorClass + "' does not provide a single XShell-argument constructor.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an instance of XShellCompletor '" + completorClass + "'.", e);
        }
    }

}
