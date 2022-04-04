package org.summerclouds.facade.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.summerclouds.common.core.console.Console;
import org.summerclouds.common.core.console.Console.COLOR;
import org.summerclouds.common.core.error.NotFoundException;
import org.summerclouds.common.core.log.MLog;
import org.summerclouds.common.core.tool.MString;
import org.summerclouds.common.core.tool.MSystem;
import org.summerclouds.common.core.tool.MSystem.ExecuteControl;
import org.summerclouds.common.core.util.MUri;

public class MvnScheme extends MLog implements Scheme {

	private File parent;

	@Override
	public File load(MUri uri) throws IOException, NotFoundException {
        File location = getArtifactLocation("MVN", uri);

        if (location.exists()) return location;

        // String mvnUrl = uri.getPath().replace('/', ':');
        String[] parts = uri.getPath().split("/");
        String group = parts[0];
        String artifact = parts[1];
        String version = parts[2];
        String ext = parts.length > 3 ? parts[3] : null;
        String classifier = parts.length > 4 ? parts[4] : null;

        assert group != null;
        assert artifact != null;
        assert version != null;

        log().i("Load Maven Resource", location, group, artifact, version, ext, classifier);

        // mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get
        // -Dartifact=com.google.guava:guava:15.0 -DrepoUrl=
        String mvnPath = cmdLocation("mvn");
        execute(
                uri.getPath(),
                parent,
                mvnPath
                        + " dependency:get"
                        + " -DartifactId="
                        + artifact
                        + " -DgroupId="
                        + group
                        + " -Dversion="
                        + version
                        + (ext == null ? "" : " -Dpackaging=" + ext)
                        + (classifier == null ? "" : " -Dclassifier=" + classifier)
                        + " -DrepoUrl=",
                true);

        if (location.exists()) return location;

        System.out.println(location);
        throw new NotFoundException("maven artifact not found", uri, location);		
	}

	@Override
	public void setParent(File parent) {
		this.parent = parent;
	}

    public File getArtifactLocation(String name, MUri uri)
            throws IOException, NotFoundException {

        File dir = getLocalRepositoryPath();

        String[] parts = uri.getPath().split("/");

        String path =
                parts[0].replace('.', '/')
                        + "/"
                        + parts[1]
                        + "/"
                        + parts[2]
                        + "/"
                        + parts[1]
                        + "-"
                        + parts[2];

        String ext = parts.length > 3 ? parts[3] : "jar";
        String classifier = parts.length > 4 ? parts[4] : null;
        if (classifier != null) path = path + "-" + classifier;
        path = path + "." + ext;

        File location = new File(dir, path);

        return location;
    }
	
    private String repositoryLocation;

    public File getLocalRepositoryPath() throws NotFoundException, IOException {
        if (repositoryLocation == null) {
            // mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout
            //			MavenCli cli = new MavenCli();
            //			cli.doMain(new String[]{"clean", "install"}, "project_dir", System.out,
            // System.out);
            String mvnPath = cmdLocation("mvn");
            repositoryLocation =
                    execute(
                            "MVN",
                            parent,
                            mvnPath
                                    + " help:evaluate -Dexpression=settings.localRepository -q -DforceStdout",
                            false)[0];
        }

        File dir = new File(repositoryLocation);
        if (!dir.exists() || !dir.isDirectory())
            throw new NotFoundException("maven local repository not found", repositoryLocation);

        return dir;
    }

    public String cmdLocation(String cmd) throws NotFoundException {
        String[] pathes = null;
        String systemPath = System.getenv("PATH");
        if (MSystem.isWindows())
            pathes = systemPath.split(";");
        else
            pathes = systemPath.split(":");

        for (String path : pathes) {
            File file = new File(path + File.separator + cmd);
            if (file.exists() && file.isFile() && file.canExecute() && file.canRead())
                return file.getAbsolutePath();
        }
        throw new NotFoundException("Command not found", cmd);
    }

    public static final Object consoleLock = new Object();

    public String[] execute(String name, File rootDir, String cmd, boolean infoOut)
            throws IOException {
        
        log().i(name, "execute", cmd, rootDir);

        final String shortName = MString.truncateNice(name, 40, 15);
        final Console console = Console.get();
        
        final StringBuilder stdOutBuilder = new StringBuilder();
        final StringBuilder stdErrBuilder = new StringBuilder();

        final boolean output = true;
        
        int exitCode =
                MSystem.execute(
                        shortName,
                        rootDir,
                        cmd,
                        new ExecuteControl() {

                            @Override
                            public void stdin(PrintWriter writer) {}

                            @Override
                            public void stdout(String line) {
                                if (output)
                                    synchronized (consoleLock) {
                                        console.print("[");
                                        console.setColor(COLOR.GREEN, null);
                                        console.print(shortName);
                                        console.cleanup();
                                        console.print("] ");
                                        console.println(line);
                                        console.flush();
                                    }
                                if (stdOutBuilder.length() > 0) stdOutBuilder.append("\n");
                                stdOutBuilder.append(line);
                            }

                            @Override
                            public void stderr(String line) {
                                if (output)
                                    synchronized (consoleLock) {
                                        console.print("[");
                                        console.setColor(COLOR.RED, null);
                                        console.print(shortName);
                                        console.cleanup();
                                        console.print("] ");
                                        console.println(line);
                                        console.flush();
                                    }
                                if (stdErrBuilder.length() > 0) stdErrBuilder.append("\n");
                                stdErrBuilder.append(line);
                            }
                        });

        String stderr = stdErrBuilder.toString();
        String stdout = stdOutBuilder.toString();
        log().i(name, "exitCode", exitCode);
        return new String[] {stdout, stderr, String.valueOf(exitCode)};
    }
    
}
