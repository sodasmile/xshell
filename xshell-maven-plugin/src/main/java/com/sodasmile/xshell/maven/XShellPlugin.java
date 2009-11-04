package com.sodasmile.xshell.maven;

import com.sodasmile.xshell.XShell;
import com.sodasmile.xshell.console.Console;
import com.sodasmile.xshell.provider.JmxConnectionProvider;
import com.sodasmile.xshell.provider.PlatformMBeanServerProvider;
import jline.Completor;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 * @goal xshell
 * @requiresProject true
 * @requiresDependencyResolution test
 */
public class XShellPlugin extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private MavenProject project;

    /**
     * Map containing configuration properties for for the {@link com.sodasmile.xshell.provider.JmxConnectionProvider}.
     *
     * @parameter
     */
    private Map<String, String> provider;

    public void execute() throws MojoExecutionException, MojoFailureException {

        @SuppressWarnings({"unchecked"})
        List<Artifact> artifacts = project.getTestArtifacts();

        ClassLoader loader = new ArtifactClassLoader(artifacts, XShellPlugin.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(loader);

        JmxConnectionProvider connectionProvider;

        if (provider != null) {

            String providerClassName = provider.get("class");
            if (providerClassName == null || providerClassName.trim().equals("")) {
                throw new IllegalStateException("A non-empty 'class' element is required for the provider configuration.");
            }

            try {

                @SuppressWarnings({"unchecked"})
                Class<JmxConnectionProvider> providerClass = (Class<JmxConnectionProvider>) loader.loadClass(providerClassName);
                try {

                    Constructor<JmxConnectionProvider> constructor = providerClass.getConstructor(Map.class);
                    try {
                        connectionProvider = constructor.newInstance(provider);
                    } catch (Exception e) {
                        throw new RuntimeException("Unable to instantiate provider '" + providerClassName + "'.", e);
                    }

                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("A JmxConnectionProvider implementation is required to provide a constructor that takes a single Map argument.", e);
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load provider class '" + providerClassName + "'.", e);
            }


        } else {
            connectionProvider = new PlatformMBeanServerProvider();
            getLog().info("No provider specified, defaulting to '" + PlatformMBeanServerProvider.class.getName() + "'.");
        }

        getLog().debug("Using JMX provider '" + provider.getClass().getName() + "'.");

        try {

            Thread thread = startShell(connectionProvider);
            try {
                thread.join();
            } catch (InterruptedException e) {
                // ignore.
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Unable to launch XShell.", e);
        }

    }

    private Thread startShell(final JmxConnectionProvider connectionProvider) throws Exception {

        Thread thread = new Thread() {
            @Override
            public void run() {

                Completor completor = new XShell.CommandCompletor();
                Console console = new PluginConsole(System.in, System.out, completor, getLog());

                try {
                    XShell shell = null;//new XShell(connectionProvider, console);
                    shell.run();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to launch XShell instance.", e);
                }

            }
        };
        thread.start();
        return thread;
    }

}
