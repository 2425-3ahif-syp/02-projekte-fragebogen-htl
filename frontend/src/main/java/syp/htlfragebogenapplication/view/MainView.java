package syp.htlfragebogenapplication.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import syp.htlfragebogenapplication.controllers.MainViewController;

public class MainView extends BorderPane {

    private final GridPane testGrid;
    private final TextField searchField;
    private final MainViewController controller;

    public MainView(MainViewController controller) {
        this.controller = controller;

        VBox topBox = new VBox();

        // Header
        HBox header = new HBox();
        header.setId("header");
        Label headerLabel = new Label("VORHANDENE AUFNAHMETESTS :");
        headerLabel.setId("header-label");
        headerLabel.setFont(new Font("System Bold", 18.0));
        header.getChildren().add(headerLabel);

        // Search Box
        HBox searchBox = new HBox(10);
        searchBox.setId("search-box");
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        searchField = new TextField();
        searchField.setId("search-field");
        searchField.setPromptText("Suchen...");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().add(searchField);

        topBox.getChildren().addAll(header, searchBox);

        testGrid = new GridPane();
        testGrid.setHgap(20);
        testGrid.setVgap(20);
        testGrid.setAlignment(Pos.CENTER);
        testGrid.getStyleClass().add("test-grid");

        for (int i = 0; i < 5; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(20);
            testGrid.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < 3; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setPercentHeight(33.33);
            testGrid.getRowConstraints().add(rowConstraints);
        }

        this.setTop(topBox);
        this.setCenter(testGrid);

        connectController();
    }

    private void connectController() {
        controller.setTestGrid(testGrid);
        controller.setSearchField(searchField);

        controller.initialize();
    }

    public GridPane getTestGrid() {
        return testGrid;
    }

    public TextField getSearchField() {
        return searchField;
    }
}
