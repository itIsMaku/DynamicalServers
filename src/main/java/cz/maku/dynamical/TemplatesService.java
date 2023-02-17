package cz.maku.dynamical;

import com.google.common.collect.Lists;
import cz.maku.mommons.worker.annotation.Initialize;
import cz.maku.mommons.worker.annotation.Service;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

@Service
public class TemplatesService {

    private final Logger log = Logger.getLogger(DynamicalServers.class.getName());
    @Getter
    private List<Template> templates;

    @Initialize
    public void init() {
        templates = Lists.newArrayList();
        File folder = new File("templates" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                log.severe("Templates folder was not created.");
                return;
            }
        }
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                log.severe("Template must be folder. Passing " + file.getName() + ".");
                continue;
            }
            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream("templates" + File.separator + file.getName() + File.separator + "template.config")) {
                properties.load(fileInputStream);
            } catch (FileNotFoundException e) {
                log.severe("Configuration file for template " + file.getName() + " was not found.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            templates.add(new Template(templates.size() + 1, file.getName(), properties));
        }
        log.info(String.valueOf(templates.size()));
    }

    public Optional<Template> getTemplate(String template) {
        return templates.stream().filter(templateObject -> templateObject.getFolder().equalsIgnoreCase(template)).findFirst();
    }

}
