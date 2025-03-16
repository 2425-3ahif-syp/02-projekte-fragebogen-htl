package syp.htlfragebogenapplication.database;

import org.h2.tools.Server;

import java.io.File;
import java.sql.SQLException;

public class H2Server {
    private static Server server;

    public static void start() {
        if (server == null) {
            try {
                String baseDir = new File("../backend/db").getCanonicalPath();
                server = Server.createTcpServer("-ifNotExists", "-baseDir", baseDir).start();
                System.out.println("TCP server running at tcp://127.0.1.1:46757 (only local connections)\n" +
                        "PG server running at pg://127.0.1.1:5435 (only local connections)\n" +
                        "Web Console server running at http://127.0.1.1:8082 (only local connections)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop();
            System.out.println("H2 Server stopped.");
            server = null;
        }
    }
}
