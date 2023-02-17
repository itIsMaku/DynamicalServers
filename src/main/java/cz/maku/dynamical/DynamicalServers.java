package cz.maku.dynamical;

import cz.maku.mommons.storage.database.type.MySQL;
import cz.maku.mommons.worker.Worker;

import java.io.File;
import java.util.logging.Logger;

public class DynamicalServers {

    public static Logger log = Logger.getLogger(DynamicalServers.class.getName());

    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.setPublicMySQL(new MySQL("127.0.0.1", 3306, "mommons", "root", "", false, true));
        worker.registerServices(TemplatesService.class, ServersService.class);
        worker.initialize();
        MySQL.getApi().connect();
    }

    public static File getServerFolder(String server) {
        return new File("servers" + File.separator + server + File.separator);
    }
}