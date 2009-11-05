package com.sodasmile.xshell;

import com.sodasmile.xshell.provider.JmxConnectionProvider;
import com.sodasmile.xshell.provider.JndiJmxConnectionProvider;
import com.sodasmile.xshell.provider.PlatformMBeanServerProvider;

import java.lang.reflect.Constructor;

/**
* @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
*/
public enum Provider {
    jndi(JndiJmxConnectionProvider.class),
    rmi(PlatformMBeanServerProvider.class); // This one does not make any sense so far.

    private Class<? extends JmxConnectionProvider> providerClass;

    Provider(Class<? extends JmxConnectionProvider> provider) {
        this.providerClass = provider;
    }

    @SuppressWarnings({"unchecked"})
    public <P extends JmxConnectionProvider> P create(final String[] args) {

        try {

            Constructor<P> constructor = (Constructor<P>) providerClass.getDeclaredConstructor(String[].class);

            return constructor.newInstance(new Object[]{args});

        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate provider '" + providerClass.getName() + "'.", e);
        }

    }

}
