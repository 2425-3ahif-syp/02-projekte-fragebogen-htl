package syp.htlfragebogenapplication;

/**
 * Launcher class for JavaFX application to avoid module system conflicts.
 * This class ensures the application can run on regular JDK without JavaFX.
 */
public class Launcher {
    public static void main(String[] args) {
        // Set system properties to help with JavaFX initialization
        System.setProperty("javafx.preloader", "");
        System.setProperty("glass.platform", "gtk");
        
        // Launch the main JavaFX application
        MainApp.main(args);
    }
}
