package cz.maku.dynamical;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

@Getter
public class Template {

    private final int id;
    private final String folder;
    private final Properties configProperties;
    private final File templateFolder;
    private String runnableFile;

    public Template(int id, String folder, Properties configProperties) {
        this.id = id;
        this.folder = folder;
        this.configProperties = configProperties;
        this.templateFolder = new File("templates" + File.separator + folder);
    }

    public boolean copy(String server) {
        try {
            File serverFolder = DynamicalServers.getServerFolder(server);
            serverFolder.mkdirs();
            for (File templateFile : templateFolder.listFiles()) {
                if (templateFile.getName().endsWith("template.config")) {
                    continue;
                }
                Files.copy(
                        templateFile.toPath(),
                        new File(
                                serverFolder,
                                templateFile.getName().replace("templates" + File.separator, "servers" + File.separator) // dont care lol
                        ).toPath());
            }
            return true;
        } catch (IOException e) {
            DynamicalServers.log.severe("Copying folder from template to server was not successful.");
            e.printStackTrace();
            return false;
        }
    }

    public void identifyRunnableFile() {
        String file = configProperties.getProperty("file");
        if (file != null) {
            runnableFile = file;
            return;
        }
        String remoteFileUrl = configProperties.getProperty("remote-file");
        try {
            File target = new File(templateFolder + File.separator + "runnable.jar");
            cz.maku.mommons.utils.Files.download(new URL(remoteFileUrl), target);
            runnableFile = "runnable";
        } catch (IOException e) {
            DynamicalServers.log.severe("Downloading file from " + remoteFileUrl + " was not successful.");
            e.printStackTrace();
        }
    }
}
