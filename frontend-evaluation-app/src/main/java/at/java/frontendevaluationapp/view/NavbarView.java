package at.java.frontendevaluationapp.view;

import at.java.frontendevaluationapp.controller.NavbarViewController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavbarView extends HBox {

    private final Button uploadButton;
    private final Button overviewButton;
    private final NavbarViewController controller;

    public NavbarView(NavbarViewController controller) {
        this.controller = controller;

        this.setPadding(new Insets(10, 20, 10, 20));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("navbar");

        // Define buttons
        uploadButton = new Button("Datei-Upload");
        overviewButton = new Button("Klassenübersicht");

        uploadButton.getStyleClass().add("nav-btn");
        overviewButton.getStyleClass().add("nav-btn");

        uploadButton.setTooltip(new Tooltip("Test-Ergebnisse hochladen"));
        overviewButton.setTooltip(new Tooltip("Auswertung aller Tests"));

        Region spacer = new Region(); // pushes overviewButton to the right if needed
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.getChildren().addAll(uploadButton, spacer, overviewButton);

        connectController();
    }

    private void connectController() {
        uploadButton.setOnAction(e -> controller.onFileUploadClicked());
        overviewButton.setOnAction(e -> controller.onOverviewClicked());
    }
}
