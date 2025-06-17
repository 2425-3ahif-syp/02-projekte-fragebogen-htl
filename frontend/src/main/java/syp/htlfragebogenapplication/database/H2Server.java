package syp.htlfragebogenapplication.database;

import org.h2.tools.Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class H2Server {
    private static Server server;
    private static File tempDbDir;

    public static void start() {
        if (server == null) {
            try {
                // Try to find external database first (for development)
                File cwd = new File(System.getProperty("user.dir"));
                File projectRoot = null;
                File dbDir = null;

                if (new File(cwd, "backend/db").exists()) {
                    projectRoot = cwd;
                    dbDir = new File(projectRoot, "backend/db");
                } else if (new File(cwd, "../backend/db").exists()) {
                    projectRoot = cwd.getParentFile();
                    dbDir = new File(projectRoot, "backend/db");
                }

                String baseDir;
                
                if (dbDir != null && dbDir.exists()) {
                    // Use external database (development mode)
                    baseDir = dbDir.getCanonicalPath();
                    System.out.println("Using external database: " + baseDir);
                } else {
                    // Extract database from JAR resources (production mode)
                    baseDir = extractDatabaseFromResources();
                    System.out.println("Using embedded database: " + baseDir);
                }

                server = Server.createTcpServer("-ifNotExists", "-baseDir", baseDir).start();

                String serverInfo = """
                        TCP server running at tcp://127.0.1.1:46757 (only local connections)
                        PG server running at pg://127.0.1.1:5435 (only local connections)
                        Web Console server running at http://127.0.1.1:8082 (only local connections)
                        """;

                System.out.println(serverInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String extractDatabaseFromResources() throws IOException {
        // Create temporary directory for database files
        tempDbDir = Files.createTempDirectory("h2db").toFile();
        tempDbDir.deleteOnExit();

        // Extract database file from resources
        InputStream dbStream = H2Server.class.getResourceAsStream("/db/questionnaireDb.mv.db");
        if (dbStream != null) {
            File tempDbFile = new File(tempDbDir, "questionnaireDb.mv.db");
            tempDbFile.deleteOnExit();
            
            Files.copy(dbStream, tempDbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dbStream.close();
            
            System.out.println("Extracted database to: " + tempDbFile.getAbsolutePath());
        } else {
            System.out.println("No embedded database found in resources, creating new database");
        }

        return tempDbDir.getCanonicalPath();
    }

    public static void stop() {
        if (server != null) {
            server.stop();
            System.out.println("H2 Server stopped.");
            server = null;
        }
        
        // Clean up temporary database directory
        if (tempDbDir != null && tempDbDir.exists()) {
            deleteDirectory(tempDbDir);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
