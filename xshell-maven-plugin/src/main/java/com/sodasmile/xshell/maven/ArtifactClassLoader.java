package com.sodasmile.xshell.maven;

import org.apache.maven.artifact.Artifact;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:runepeter@jforce.no">Rune Peter Bj&oslash;rnstad</a>
 */
public final class ArtifactClassLoader extends URLClassLoader {

    public ArtifactClassLoader(final List<Artifact> artifacts, final ClassLoader parent) {
        super(toArray(artifacts), parent);
    }

    private static URL[] toArray(final List<Artifact> artifacts) {

        List<URL> urls = new ArrayList<URL>();

        for (Artifact artifact : artifacts) {

            try {
                URL url = artifact.getFile().toURI().toURL();
                urls.add(url);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Unable to resolve URL for artifact '" + artifact + "'.");
            }

        }

        URL[] result = new URL[urls.size()];

        return urls.toArray(result);
    }

}
