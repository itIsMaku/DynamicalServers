package cz.maku.dynamical;

import cz.maku.mommons.storage.database.type.MySQL;
import cz.maku.mommons.worker.Worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class DynamicalServers {

    public static Logger log = Logger.getLogger(DynamicalServers.class.getName());
    public static Properties appProperties = new Properties();

    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("app.config")) {
            appProperties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String ip = appProperties.getProperty("mysql-address");
        int port = Integer.parseInt(appProperties.getProperty("mysql-port"));
        String database = appProperties.getProperty("mysql-database");
        String username = appProperties.getProperty("mysql-username");
        String password = appProperties.getProperty("mysql-password");
        boolean ssl = Boolean.parseBoolean(appProperties.getProperty("mysql-ssl"));
        boolean autoReconnect = Boolean.parseBoolean(appProperties.getProperty("mysql-auto-reconnect"));
        MySQL mySQL = new MySQL(ip, port, database, username, password, ssl, autoReconnect);
        mySQL.connect();
        Worker worker = new Worker();
        worker.setPublicMySQL(mySQL);
        worker.registerServices(TemplatesService.class, ServersService.class);
        worker.initialize();
    }

    public static File getServerFolder(String server) {
        return new File("servers" + File.separator + server + File.separator);
    }
}