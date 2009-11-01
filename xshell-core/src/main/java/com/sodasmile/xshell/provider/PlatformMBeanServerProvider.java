package com.sodasmile.xshell.provider;

import javax.management.MBeanServerConnection;
import java.lang.management.ManagementFactory;

/**
 * Provides connectivity to the current Platform {@link javax.management.MBeanServer}.
 *
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class PlatformMBeanServerProvider implements JmxConnectionProvider {
    public MBeanServerConnection connect() {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
