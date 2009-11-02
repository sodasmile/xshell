package com.sodasmile.xshell.provider;

import javax.management.MBeanServerConnection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Looks up a {@link javax.management.MBeanServerConnection} using JNDI.
 *
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class JndiJmxConnectionProvider implements JmxConnectionProvider {

    /**
     * The JNDI {@link javax.naming.Context} to use for looking up an {@link javax.management.MBeanServerConnection}.
     */
    private Context context;

    /**
     * The provider configuration elements.
     */
    private final Map<String, String> configuration;

    /**
     * The JNDI-bound name of the {@link javax.management.MBeanServerConnection}.
     */
    private final String name;

    public JndiJmxConnectionProvider(final Map<String, String> configuration) {

        this.configuration = configuration;
        if (configuration == null) {
            throw new IllegalArgumentException("Provider configuration cannot be 'null'.");
        }

        this.name = configuration.get("name");
        if (name == null || name.trim().equals("")) {
            throw new IllegalArgumentException("The JNDI-provider requires a non-empty 'name' configuration element.");
        }

    }

    public MBeanServerConnection connect() {

        if (context == null) {

            try {
                this.context = new InitialContext(new Hashtable(configuration));
            } catch (NamingException e) {
                throw new RuntimeException("Unable to initialize JNDI-context.", e);
            }

        }

        try {

            return (MBeanServerConnection) context.lookup(name);

        } catch (NamingException e) {
            throw new IllegalStateException("Unable to lookup JNDI-resource '" + name + "'.", e);
        }

    }

}

