package com.sodasmile.xshell;

import com.sodasmile.xshell.args.Option;
import com.sodasmile.xshell.args.Options;
import com.sodasmile.xshell.command.Command;
import com.sodasmile.xshell.console.Console;
import com.sodasmile.xshell.console.JLineConsole;
import com.sodasmile.xshell.console.completor.XShellCompletor;
import com.sodasmile.xshell.provider.JmxConnectionProvider;
import jline.Completor;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.*;

/**
 * Shell for å kommunisere mot Larm JMX kontrollere.
 */
public class XShell {

    private static final String welcometext =
            "\n\n__    ___    _____    ___  ___  ______  ___    ___\n"
                    + "\\  \\ /  /   /     \\  /  / /  / /  ___/ /  /   /  /\n"
                    + " \\  \"  /   |   /^-/ /  /_/  / /  /_   /  /   /  /\n"
                    + "  \\   / __  \\  \\   /  __   / /  __/  /  /   /  /\n"
                    + " /  ^ \\ \\  \\'   | /  / /  / /  /__  /  /__ /  /__\n"
                    + "/__/ \\_\\ \\_____/ /__/ /__/ /_____/ /_____//_____/";

    @Option(name = "p", description = "The connection provider identifier.", required = true)
    private Provider provider = null;

    @Option(name = "cp", description = "The classpath to use for the shell instance.", delimiter = ':')
    private String[] classpath = {};

    @Option(name = "domain", description = "The default domain used by the shell.")
    private String defaultDomain = null;

    @Option(name = "mbean", description = "The default mbean used by the shell.")
    private String defaultMBean = null;

    private Console console;

    private XShellCompletor completor;

    private JmxConnectionProvider connectionProvider;

    private MBeanServerConnection connection;

    private State state = State.DISCONNECTED;

    private String[] domains = {};

    private Map<String, ObjectName> mbeans = new HashMap<String, ObjectName>();

    private Map<String, MBeanOperationInfo> operations;

    private String currentMBean = null;

    private XShell(final String[] args) {

        Options.apply(this, args);

        initClassLoader();
        initConnectionProvider(args);
        initCompletor();
        initConsole();

        connect();
    }

    private void initConnectionProvider(final String[] args) {
        this.connectionProvider = provider.create(args);
    }

    private void initCompletor() {
        this.completor = state.create(this);
    }

    private void initConsole() {
        this.console = new JLineConsole(System.in, System.out, completor);
    }

    private void initClassLoader() {

        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(new XShellClassLoader(classpath, currentClassLoader));
    }

    private void setState(final State newState) {
        if (!this.state.equals(newState)) {
            this.state = newState;
            setCompletor(newState.create(this));
        }
    }

    private void setCompletor(final XShellCompletor completor) {

        @SuppressWarnings({"unchecked"})
        Collection<Completor> completors = console.consoleReader().getCompletors();

        for (Completor c : new ArrayList<Completor>(completors)) {
            console.consoleReader().removeCompletor(c);
        }

        console.consoleReader().addCompletor(completor);
        this.completor = completor;
    }

    public void nonKeywordInvoke(final String[] arguments) {

        if (arguments.length == 0) {
            throw new IllegalArgumentException("Cannot invoke <nothing>.");
        }

        if ("..".equals(arguments[0])) {

            switch (state) {
                case INSIDE_MBEAN:
                    String domain = currentMBean.substring(0, currentMBean.indexOf(':'));
                    domain(domain);
                    break;
                default:
                    console.setPrompt("xshell");
                    setState(State.CONNECTED);
            }

        } else {

            if (arguments.length > 1) {

                String[] args = new String[arguments.length - 1];
                System.arraycopy(arguments, 1, args, 0, arguments.length - 1);

                invokeOperation(arguments[0], args);
            } else {
                invokeOperation(arguments[0], new String[]{});
            }
        }
    }

    public void invokeOperation(final String operation, final String[] arguments) {

        final ObjectName mbean = mbeans.get(currentMBean);

        // TODO runepeter: Currently only support String argument operations. Get a reference to MBeanOperationInfo!!
        String[] types = new String[arguments.length];
        Arrays.fill(types, "java.lang.String");

        try {
            Object result = connection.invoke(mbean, operation, arguments, types);

            // TODO runepeter: Make use of MBeanOperationInfo to handle void method output properly.
            System.out.println(operation + " " + Arrays.toString(arguments) + " -> " + result);

        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke operation '" + operation + "' on MBean '" + currentMBean + "'.", e);
        }

    }

    public void run() throws Exception {

        console.writeInfo(welcometext);

        String input;

        //System.out.println(welcometext);

        while ((input = console.readLine()) != null) {

            console.writeInfo("Yo Mastar! You told me to '" + input + "'. I'll do my best Sir.");

            Command command = toCommand(input);
            command.execute(this);
        }
    }

    private Command toCommand(final String input) {

        String[] parts = input.split(" ");

        if (Keyword.isKeyword(parts[0])) {

            String[] arguments = {};

            if (parts.length > 1) {
                arguments = new String[parts.length - 1];
                System.arraycopy(parts, 1, arguments, 0, parts.length - 1);
            }

            return Keyword.valueOf(parts[0]).command(arguments);

        } else {
            return Keyword.NULL.command(parts);
        }

    }

    public void connect() {

        try {
            this.connection = connectionProvider.connect();
            setDomains(connection.getDomains());

            if (defaultMBean != null && !defaultMBean.trim().equals("")) {
                mbean(defaultMBean);
            } else if (defaultDomain != null && !defaultDomain.trim().equals(" ")) {
                domain(defaultDomain);
            } else {
                setState(State.CONNECTED);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to connect to provider.", e);
        }

    }

    public void domain(final String domain) {

        this.currentMBean = null;
        console.setPrompt(domain);

        try {

            Map<String, ObjectName> map = new HashMap<String, ObjectName>();

            @SuppressWarnings({"unchecked"}) Set<ObjectName> objectNames = connection.queryNames(new ObjectName(domain + ":*"), null);
            for (ObjectName objectName : objectNames) {
                map.put(objectName.getCanonicalName(), objectName);
            }

            setMBeans(map);

        } catch (Exception e) {
            throw new RuntimeException("Unable to query for MBean names in domain '" + domain + "'.", e);
        }

        setState(State.INSIDE_DOMAIN);
    }

    public void setDomains(final String[] domains) {
        this.domains = domains;
    }

    public String[] domains() {
        return domains;
    }

    public void mbean(final String mbean) {

        final MBeanInfo info;
        try {
            info = connection.getMBeanInfo(new ObjectName(mbean));
        } catch (Exception e) {
            throw new RuntimeException("Cannot resolve MBean info for '" + mbean + "'.", e);
        }

        this.currentMBean = mbean;
        console.setPrompt(mbean);

        Map<String, MBeanOperationInfo> map = new HashMap<String, MBeanOperationInfo>();

        for (MBeanOperationInfo operationInfo : info.getOperations()) {
            map.put(operationInfo.getName(), operationInfo);
        }

        setOperations(map);

        setState(State.INSIDE_MBEAN);
    }

    public void setMBeans(final Map<String, ObjectName> mbeans) {
        this.mbeans = mbeans;
    }

    public String[] mbeans() {

        Set<String> set = mbeans.keySet();
        String[] names = new String[set.size()];

        return new ArrayList<String>(set).toArray(names);
    }

    public void setOperations(final Map<String, MBeanOperationInfo> operations) {
        this.operations = operations;
    }

    public MBeanOperationInfo[] operations() {

        Collection<MBeanOperationInfo> operationInfo = operations.values();
        MBeanOperationInfo[] array = new MBeanOperationInfo[operationInfo.size()];

        return new ArrayList<MBeanOperationInfo>(operationInfo).toArray(array);
    }

    public static void main(final String[] args) throws Exception {

        XShell me = new XShell(args);
        me.run();

    }

}
