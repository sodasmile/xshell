package com.sodasmile.xshell.maven;

import com.sodasmile.xshell.XShell;
import com.sodasmile.xshell.provider.JmxConnectionProvider;
import com.sodasmile.xshell.provider.JndiJmxConnectionProvider;
import com.sodasmile.xshell.provider.PlatformMBeanServerProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
            XShell shell = new XShell(connectionProvider);
            shell.execute();
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to launch XShell.", e);
        }

    }

    private void pikk(String jalla, ClassLoader loader1) {
        try {
            Class<?> clazz = loader1.loadClass("org.jboss.jmx.adaptor.rmi.RMIAdaptor");
            System.err.println("\n##\n##\n## " + jalla + " SHELL IS RUNNING :: " + clazz + "\n##\n##\n##");
        } catch (ClassNotFoundException e) {
            System.err.println("ERR: " + e.getMessage());
        }
    }

    

}
