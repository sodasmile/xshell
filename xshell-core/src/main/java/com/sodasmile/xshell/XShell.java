package com.sodasmile.xshell;

import com.sodasmile.xshell.provider.JmxConnectionProvider;
import jline.ANSIBuffer;
import jline.Completor;
import jline.ConsoleReader;

import javax.management.*;
import javax.naming.InitialContext;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    //private RMIAdaptor server;
    private MBeanServerConnection server;

    private String objectName = "larm:name=Tennet,type=Management";
    private LarmCompletor larmCompletor;
    private Hashtable<String, String> jndiEnvironment;
    private static final String DEFAULT_PORT = ":1099";

    private final JmxConnectionProvider provider;

    public XShell(final JmxConnectionProvider provider) throws Exception {
        this.provider = provider;

        jndiEnvironment = new Hashtable<String, String>();
        jndiEnvironment.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        jndiEnvironment.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        jndiEnvironment.put("java.naming.provider.url", "localhost" + DEFAULT_PORT);

//        InitialContext ic = new InitialContext(jndiEnvironment);
//        server = (RMIAdaptor) ic.lookup("jmx/invoker/RMIAdaptor");
        larmCompletor = new LarmCompletor();
    }

    private void sendKommando(String kommando) throws Exception {
        if (kommando.equals("cd norned")) {
            objectName = "larm:name=NorNed,type=Management";
            printinfo();
            return;
        } else if (kommando.equals("cd tennet")) {
            objectName = "larm:name=Tennet,type=Management";
            printinfo();
            return;
        } else if (kommando.startsWith("connect")) {
            String[] s = kommando.split("\\s+");
            // s[0] == connect
            // s[1] == ex: localhost[:1099]
            if (s.length > 1) { // host specified
                if (s[1].indexOf(":") != -1) {
                    jndiEnvironment.put("java.naming.provider.url", s[1]);
                } else {
                    jndiEnvironment.put("java.naming.provider.url", s[1] + ":" + DEFAULT_PORT);
                }
            } else {
                // not specified host
                jndiEnvironment.put("java.naming.provider.url", "localhost" + DEFAULT_PORT);
            }

            try {
                InitialContext ic = new InitialContext(jndiEnvironment);
                server = (MBeanServer) ic.lookup("jmx/invoker/RMIAdaptor");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        } else if (kommando.equals("demo")) {
            kommando = "demodag";
        } else if (kommando.equals("spk")) {
            kommando = "sendPositivKvitteringsmeldingForNeste";
        } else if (kommando.equals("reload")) {
            kommando = "redeployProcessDefinitions";
        } else if (kommando.equals("sendnp")) {
            kommando = "sendNominertProgramForNesteDøgn";
        }

        invokeThis(kommando);
    }

    private void invokeThis(final String kommando) {
        try {
            ObjectName object = new ObjectName(objectName);

            if (kommando.indexOf(" ") != -1) {
                String parameterbit = kommando.replaceFirst("^.+?\\s+", "");
                if (parameterbit.indexOf(" ") != -1) {
                    throw new IllegalArgumentException("Støtter foreløpig kun ett parameter");
                }
                String operasjon = kommando.replaceAll("\\s+.*$", "");

//                MBeanInfo mBeanInfo = server.getMBeanInfo(object);
//                MBeanOperationInfo[] infos = mBeanInfo.getOperations();
//

                server.invoke(object, operasjon, new Object[]{parameterbit}, new String[]{"java.lang.String"});
            } else {
                server.invoke(object, kommando, null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printinfo() {
        try {
            Set<String> operations = new TreeSet<String>();
            MBeanInfo info = server.getMBeanInfo(new ObjectName(objectName));
            final MBeanOperationInfo[] opInfo = info.getOperations();
            for (MBeanOperationInfo op : opInfo) {
                String returnType = op.getReturnType();
                String opName = op.getName();
                operations.add(opName);
                System.out.print(" + " + returnType + " " + opName + "(");

                MBeanParameterInfo[] params = op.getSignature();
                for (int p = 0; p < params.length; p++) {
                    MBeanParameterInfo paramInfo = params[p];

                    String pname = paramInfo.getName();
                    String type = paramInfo.getType();

                    if (pname.equals(type)) {
                        System.out.print(type);
                    } else {
                        System.out.print(type + " " + objectName);
                    }

                    if (p < params.length - 1) {
                        System.out.print(',');
                    }
                }
                System.out.println(")");
                larmCompletor.setOptions(operations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() throws Exception {
        String shellPrompt = "lshell> ";
        ConsoleReader reader = new ConsoleReader();

        if (reader.getTerminal().isANSISupported()) {
            ANSIBuffer ansi = new ANSIBuffer();
            shellPrompt = ansi.yellow("xshell> ").toString();
        }

        String linje;

        reader.addCompletor(larmCompletor);
        reader.setDefaultPrompt(shellPrompt);

        System.out.println(welcometext);

        while ((linje = reader.readLine()) != null) {
            System.out.println("Yo Mastar! You told me to '" + linje + "'. I'll do my best Sir.");
            if (linje.equals("avslutt")) {
                break;
            }
            if (linje.equals("help")) {
                printHelp();
            } else if (linje.equals("pwd")) {
                System.out.println(objectName);
            } else if (linje.equals("ls")) {
                printinfo();
            } else {
                sendKommando(linje);
            }
        }
    }

    private void printHelp() {
        PrintStream out = System.out;
        out.println("Velkommen til Larm Shell");
        out.println(" trykk [TAB] for å se tilgjengelige kommandoer");
        out.println(" skriv avslutt for å avslutte programmet");
        out.println("");
    }

    private static class LarmCompletor implements Completor {
        private Set<String> options = new TreeSet<String>();

        private LarmCompletor() {
            addDefaultOptions();
        }

        public void setOptions(final Set<String> options) {
            this.options = options;
            addDefaultOptions();
        }

        private void addDefaultOptions() {
            options.add("cd norned");
            options.add("cd tennet");
            options.add("avslutt");
            options.add("connect");
            options.add("help");

        }

        public int complete(final String commandFromShell, final int pos, final List candidatesListOutVariable) {

            for (String option : options) {
                if (option.toLowerCase().startsWith(commandFromShell.toLowerCase())) {
                    //noinspection unchecked
                    candidatesListOutVariable.add(option);
                }
            }

            return 0; // how many letters matched? What is this really used for???
        }
    }

    public static void main(final String[] args) throws Exception {

        XShell me = new XShell(null);
        me.execute();
    }
}
