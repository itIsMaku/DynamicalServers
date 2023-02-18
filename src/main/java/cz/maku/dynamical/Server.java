package cz.maku.dynamical;

import cz.maku.mommons.ef.repository.Repository;
import cz.maku.mommons.utils.Nets;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Getter
@Setter
public class Server {

    private final String name;
    private final Template template;
    private String address;
    private int port;

    public Server(String name, Template template) {
        this.name = name;
        this.template = template;
    }

    public void init() {
        String address;
        try {
            address = Nets.getAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Repository<String, ServerModel> repository = ServersService.getRepository();
        List<Integer> ports = repository.selectAll().stream().map(ServerModel::getPort).toList();
        int minPort = Integer.parseInt(DynamicalServers.appProperties.getProperty("servers-ports-min"));
        int maxPort = Integer.parseInt(DynamicalServers.appProperties.getProperty("servers-ports-max"));
        int[] range = IntStream.rangeClosed(minPort, maxPort).toArray();
        int port = 0;
        for (int i : range) {
            if (Nets.isAvailablePort(i) && !ports.contains(i)) {
                port = i;
            }
        }
        if (port == 0) {
            DynamicalServers.log.severe("No available port found for server " + name + "!");
            return;
        }
        this.port = port;
        this.address = address;
    }

    public boolean make() {
        template.identifyRunnableFile();
        return template.copy(name);
    }

    public CompletableFuture<Boolean> makeAsync() {
        return CompletableFuture.supplyAsync(this::make);
    }

    public void start() {
        Properties properties = template.getConfigProperties();
        if (properties.getProperty("command") == null) {
            DynamicalServers.log.severe("Command is not defined in template " + template.getFolder() + "!");
            return;
        }
        new Thread(() -> {
            String command = properties.getProperty("command");
            command = command.replace("%port%", String.valueOf(port));
            command = command.replace("%address%", address);
            command = command.replace("%name%", name);
            command = command.replace("%runnable%", template.getRunnableFile());
            try {
                Runtime runTime = Runtime.getRuntime();
                Process process = runTime.exec(command, null, new File("servers" + File.separator + name + File.separator));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.printf("[%s] %s%n", name, line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
