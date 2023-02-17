package cz.maku.dynamical;

import cz.maku.mommons.ef.Repositories;
import cz.maku.mommons.ef.repository.Repository;
import cz.maku.mommons.storage.database.type.MySQL;
import cz.maku.mommons.worker.annotation.*;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service(scheduled = true)
public class ServersService {

    private final Logger log = Logger.getLogger(DynamicalServers.class.getName());
    private Repository<String, ServerModel> repository;

    @Load
    private TemplatesService templatesService;

    @Initialize
    public void init() {
        try {
            repository = Repositories.createRepository(MySQL.getApi().getConnection(), ServerModel.class);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | SQLException e) {
            log.severe("Entity repository can not be created.");
            throw new RuntimeException(e);
        }
        File folder = new File("servers" + File.separator);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                log.severe("Servers folder was not created.");
                return;
            }
        }

        // test
        for (Template template : templatesService.getTemplates()) {
            template.identifyRunnableFile();
            template.copy(template.getFolder() + template.getId());
        }
    }

    public Optional<Server> fromModel(ServerModel serverModel) {
        Optional<Template> optionalTemplate = templatesService.getTemplate(serverModel.getTemplate());
        if (optionalTemplate.isEmpty()) {
            log.severe("Template " + serverModel.getTemplate() + "does not exist.");
            return Optional.empty();
        }
        Server server = new Server(serverModel.getName(), optionalTemplate.get());
        return Optional.of(server);
    }

    @SneakyThrows
    @Repeat(period = 5000L)
    @Async
    public void serversCreationReceiving() {
        List<ServerModel> serversForCreation = repository.selectFieldValues(Map.of("created", false));
        for (ServerModel serverModel : serversForCreation) {
            Optional<Server> optionalServer = fromModel(serverModel);
            if (optionalServer.isEmpty()) {
                serverModel.setFailure("Failure during deserializing server.");
                repository.update(serverModel);
                continue;
            }
            Server server = optionalServer.get();
            // TODO: 17.02.2023 20:05
        }
    }

}
