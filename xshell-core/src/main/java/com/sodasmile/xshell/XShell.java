package com.sodasmile.xshell;

import com.sodasmile.xshell.args.Option;
import com.sodasmile.xshell.args.Options;
import com.sodasmile.xshell.console.Console;
import com.sodasmile.xshell.console.JLineConsole;
import com.sodasmile.xshell.provider.JmxConnectionProvider;
import com.sodasmile.xshell.provider.JndiJmxConnectionProvider;
import com.sodasmile.xshell.provider.PlatformMBeanServerProvider;
import jline.Completor;

import javax.management.*;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Shell for å kommunisere mot Larm JMX kontrollere.
 */
public class XShell {

    private static final String welcometext = // TODO anderssm: Skriv XShell i stedet.
            "      ___    _____    ___  ___  ______  ___    ___\n"
                    + "     /  /   /     \\  /  / /  / /  ___/ /  /   /  /\n"
                    + "    /  /   |   /^-/ /  /_/  / /  /_   /  /   /  /\n"
                    + "   /  / __  \\  \\   /  __   / /  __/  /  /   /  /\n"
                    + "  /  /_ \\  \\'   | /  / /  / /  /__  /  /__ /  /__\n"
                    + " /____/  \\_____/ /__/ /__/ /_____/ /_____//_____/";

    public static enum Provider {
        jndi( JndiJmxConnectionProvider.class ),
        rmi( PlatformMBeanServerProvider.class ); // This one does not make any sense so far.

        private Class<? extends JmxConnectionProvider> providerClass;

        Provider( Class<? extends JmxConnectionProvider> provider ) {
            this.providerClass = provider;
        }

        @SuppressWarnings( { "unchecked" } )
        public <P extends JmxConnectionProvider> P create( final String[] args ) {

            try {

                Constructor<P> constructor = (Constructor<P>) providerClass.getDeclaredConstructor( String[].class );

                return constructor.newInstance( new Object[]{ args } );

            } catch ( Exception e ) {
                throw new RuntimeException( "Unable to instantiate provider '" + providerClass.getName() + "'.", e );
            }

        }

    }

    @Option( name = "p", description = "The connection provider identifier.", required = true )
    private Provider provider;

    @Option( name = "cp", description = "The classpath to use for the shell instance.", delimiter = ':' )
    private String[] classpath = { };

    private Console console;
    private CommandCompletor commandCompletor;
    private JmxConnectionProvider connectionProvider;
    private MBeanServerConnection connection;

    private XShell( final String[] args ) {

        Options.apply( this, args );

        initClassLoader();
        initConnectionProvider( args );
        initCommandCompletor();
        initConsole();
    }

    private void initConnectionProvider( final String[] args ) {
        this.connectionProvider = provider.create( args );
    }

    private void initCommandCompletor() {
        this.commandCompletor = new CommandCompletor();
    }

    private void initConsole() {
        this.console = new JLineConsole( System.in, System.out, commandCompletor );
    }

    private void initClassLoader() {

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( new XShellClassLoader( classpath, currentClassLoader ) );

    }

    private void sendKommando( String command ) throws Exception {

        if ( command.startsWith( "cd" ) ) {

            if ( "cd".equals( command.trim() ) ) {
                commandCompletor.reset();
            } else {

                final String name = command.substring( command.indexOf( ' ' ) );
                System.out.println( "name = " + name );

            }

            return;

        } else if ( command.equals( "cd tennet" ) ) {
            //objectName = "larm:name=Tennet,type=Management";
            printinfo();
            return;
        } else if ( command.startsWith( "connect" ) ) {

            this.connection = connectionProvider.connect();

            List<String> domainList = new ArrayList<String>();

            StringBuffer buf = new StringBuffer( "Connected - the following domains are available:\n" );
            for ( String domain : connection.getDomains() ) {
                domainList.add( domain );
                buf.append( " * " ).append( domain ).append( '\n' );
            }

            System.out.println( buf );

            String[] domains = new String[domainList.size()];
            commandCompletor.setDomains( domainList.toArray( domains ) );

            /*String[] s = command.split("\\s+");
            // s[0] == connect
            // s[1] == ex: localhost[:1099]
            if (s.length > 1) { // host specified
                if (s[1].indexOf(":") != -1) {
                    jndiEnvironment.put("java.naming.connectionProvider.url", s[1]);
                } else {
                    jndiEnvironment.put("java.naming.connectionProvider.url", s[1] + ":" + DEFAULT_PORT);
                }
            } else {
                // not specified host
                jndiEnvironment.put("java.naming.connectionProvider.url", "localhost" + DEFAULT_PORT);
            }

            try {
                InitialContext ic = new InitialContext(jndiEnvironment);
                connection = (MBeanServer) ic.lookup("jmx/invoker/RMIAdaptor");
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return;

        } else if ( command.equals( "demo" ) ) {
            command = "demodag";
        } else if ( command.equals( "spk" ) ) {
            command = "sendPositivKvitteringsmeldingForNeste";
        } else if ( command.equals( "reload" ) ) {
            command = "redeployProcessDefinitions";
        } else if ( command.equals( "sendnp" ) ) {
            command = "sendNominertProgramForNesteDøgn";
        }

        invokeThis( command );
    }

    private void invokeThis( final String kommando ) {
        try {
            ObjectName object = null;//new ObjectName( objectName );

            if ( kommando.indexOf( " " ) != -1 ) {
                String parameterbit = kommando.replaceFirst( "^.+?\\s+", "" );
                if ( parameterbit.indexOf( " " ) != -1 ) {
                    throw new IllegalArgumentException( "Støtter foreløpig kun ett parameter" );
                }
                String operasjon = kommando.replaceAll( "\\s+.*$", "" );

//                MBeanInfo mBeanInfo = connection.getMBeanInfo(object);
//                MBeanOperationInfo[] infos = mBeanInfo.getOperations();
//

                connection.invoke( object, operasjon, new Object[]{ parameterbit }, new String[]{ "java.lang.String" } );
            } else {
                connection.invoke( object, kommando, null, null );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void printinfo() {
        try {
            Set<String> operations = new TreeSet<String>();
            MBeanInfo info = connection.getMBeanInfo( null );//new ObjectName( objectName ) );
            final MBeanOperationInfo[] opInfo = info.getOperations();
            for ( MBeanOperationInfo op : opInfo ) {
                String returnType = op.getReturnType();
                String opName = op.getName();
                operations.add( opName );
                System.out.print( " + " + returnType + " " + opName + "(" );

                MBeanParameterInfo[] params = op.getSignature();
                for ( int p = 0; p < params.length; p++ ) {
                    MBeanParameterInfo paramInfo = params[p];

                    String pname = paramInfo.getName();
                    String type = paramInfo.getType();

                    if ( pname.equals( type ) ) {
                        System.out.print( type );
                    } else {
                        System.out.print( type + " " + null );//objectName );
                    }

                    if ( p < params.length - 1 ) {
                        System.out.print( ',' );
                    }
                }
                System.out.println( ")" );
                commandCompletor.setOptions( operations );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {

        console.writeInfo( welcometext );

        String linje;

        //System.out.println(welcometext);

        while ( ( linje = console.readLine() ) != null ) {
            console.writeInfo( "Yo Mastar! You told me to '" + linje + "'. I'll do my best Sir." );
            if ( linje.equals( "avslutt" ) ) {
                break;
            }
            if ( linje.equals( "help" ) ) {
                printHelp();
            } else if ( linje.equals( "pwd" ) ) {
                System.out.println( "JALLA" );//objectName );
            } else if ( linje.equals( "ls" ) ) {
                printinfo();
            } else {
                sendKommando( linje );
            }
        }
    }

    private void printHelp() {
        PrintStream out = System.out;
        out.println( "Velkommen til Larm Shell" );
        out.println( " trykk [TAB] for å se tilgjengelige kommandoer" );
        out.println( " skriv avslutt for å avslutte programmet" );
        out.println( "" );
    }

    private enum Keyword {
        cd, connect, quit;

        public static Keyword[] candidates( final String input ) {

            List<Keyword> candidates = new ArrayList<Keyword>();

            for ( Keyword keyword : values() ) {
                if ( keyword.name().startsWith( input ) ) {
                    candidates.add( keyword );
                }
            }

            Keyword[] array = new Keyword[candidates.size()];
            return candidates.toArray( array );
        }

        public static boolean isKeyword( final String input ) {

            for ( Keyword keyword : values() ) {
                if ( keyword.name().equals( input ) ) {
                    return true;
                }
            }

            return false;
        }

    }

    public static class CommandCompletor implements Completor {

        private Set<String> options = new TreeSet<String>();

        private String[] domains = { };

        public CommandCompletor() {
            addDefaultOptions();
        }

        public void setDomains( final String[] domains ) {
            this.domains = domains;
        }

        public void setOptions( final Set<String> options ) {
            this.options = options;
            addDefaultOptions();
        }

        private void addDefaultOptions() {
            options.add( "cd" );
            options.add( "quit" );
            options.add( "connect" );
            options.add( "help" );
        }

        void reset() {
            this.options = new TreeSet<String>();
            addDefaultOptions();
        }

        public int complete( final String commandFromShell, final int pos, final List candidatesListOutVariable ) {

            final String input = commandFromShell.toLowerCase();

            if ( Keyword.isKeyword( input.indexOf( " " ) == -1 ? input : input.substring( 0, input.indexOf( " " ) ) ) ) {

                System.err.println( "k.wrd" );

            } else {

                Keyword[] keywords = Keyword.candidates( input );

                for ( Keyword candidate : keywords ) {
                    //noinspection unchecked
                    candidatesListOutVariable.add( candidate.name() );
                }

            }

            return 0; // how many letters matched? What is this really used for???
        }
    }

    private static class XShellClassLoader extends URLClassLoader {

        private static Logger log = Logger.getLogger( XShellClassLoader.class.getName() );

        public XShellClassLoader( final String[] classpath, final ClassLoader parent ) {
            super( toUrlArray( classpath ), parent );
        }

        private static URL[] toUrlArray( final String[] classpath ) {

            URL[] urls = new URL[classpath.length];

            for ( int i = 0; i < classpath.length; i++ ) {
                String entry = classpath[i];
                try {

                    File file = new File( entry );
                    if ( !file.exists() ) {
                        log.warning( "Classpath entry '" + entry + "' does not exist." );
                    }

                    urls[i] = file.toURI().toURL();
                    log.info( "Added: " + urls[i] );

                } catch ( MalformedURLException e ) {
                    throw new IllegalArgumentException( "Unable to convert classpath entry '" + entry + "' to an URL.", e );
                }
            }

            return urls;
        }

    }

    public static void main( final String[] args ) throws Exception {

        XShell me = new XShell( args );
        me.run();

    }

}
