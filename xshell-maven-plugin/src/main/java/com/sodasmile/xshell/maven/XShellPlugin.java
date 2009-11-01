package com.sodasmile.xshell.maven;

import com.sodasmile.xshell.provider.JmxConnectionProvider;
import com.sodasmile.xshell.provider.JndiJmxConnectionProvider;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
     * Map containing configuration properties for JNDI-provider.
     *
     * @parameter
     */
    private Map jndi;

    /**
     * The configuration of {@link com.sodasmile.xshell.provider.JmxConnectionProvider} used by the XShell.
     *
     * @parameter 
     */
    private JmxConnectionProvider provider;

    public void execute() throws MojoExecutionException, MojoFailureException {

        if (jndi != null) {
            for (Object s : jndi.keySet()) {
                System.out.println(s + " = " + jndi.get(s));
            }
            this.provider = new JndiJmxConnectionProvider();
        }

        System.err.println("PROVIDER: " + provider);

        @SuppressWarnings({"unchecked"})
        List<Artifact> artifacts = project.getTestArtifacts();

        ArtifactClassLoader loader = new ArtifactClassLoader(artifacts, XShellPlugin.class.getClassLoader());
        pikk("jalla", loader);

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
