package com.sodasmile.xshell.provider;

import javax.management.MBeanServerConnection;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public interface JmxConnectionProvider {

    /**
     * Connects to the {@link javax.management.MBeanServer}.
     *
     * @return a {@link javax.management.MBeanServerConnection} instance.
     */
    MBeanServerConnection connect();

}
