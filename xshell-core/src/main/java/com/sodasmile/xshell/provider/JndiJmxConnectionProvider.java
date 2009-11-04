package com.sodasmile.xshell.provider;

import com.sodasmile.xshell.args.Option;
import com.sodasmile.xshell.args.Options;

import javax.management.MBeanServerConnection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Looks up a {@link javax.management.MBeanServerConnection} using JNDI.
 *
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public class JndiJmxConnectionProvider implements JmxConnectionProvider {

    @Option( name = "name", description = "The JNDI-bound name name of the MBeanServerConnection.", required = true )
    private String jndiName;

    /**
     * The JNDI {@link javax.naming.Context} to use for looking up an {@link javax.management.MBeanServerConnection}.
     */
    private Context context;

    public JndiJmxConnectionProvider( final String[] args ) {
        Options.apply( this, args );
    }

    public MBeanServerConnection connect() {

        if ( context == null ) {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Properties properties = new Properties();
            InputStream is = null;
            try {
                properties.load( is = classLoader.getResourceAsStream( "jndi.properties" ));
            } catch ( IOException e ) {
                throw new RuntimeException("Unable to load 'jndi.properties' from classpath.");
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }

            }

            try {
                this.context = new InitialContext(properties);
            } catch ( NamingException e ) {
                throw new RuntimeException( "Unable to initialize JNDI-context.", e );
            }

        }

        try {

            return (MBeanServerConnection) context.lookup( jndiName );

        } catch ( NamingException e ) {
            throw new IllegalStateException( "Unable to lookup JNDI-resource '" + jndiName + "'.", e );
        }

    }

}

