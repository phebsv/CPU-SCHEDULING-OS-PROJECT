package GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import GUI.components.*;

public class MainGUI extends Application {
    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CPU Scheduling Simulator");

        // Initialize tabs first
        tabPane = new TabPane();
        
        // Create tabs with empty content first
        Tab inputTab = new Tab("Input");
        Tab resultsTab = new Tab("Results");
        Tab metricsTab = new Tab("Metrics");
        
        // Initialize panes with tab references
        InputPane inputPane = new InputPane(tabPane, resultsTab);
        ResultsPane resultsPane = new ResultsPane();
        MetricsPane metricsPane = new MetricsPane();
        
        // Connect panes
        inputPane.setResultsPane(resultsPane);
        inputPane.setMetricsPane(metricsPane);
        
        // Set tab content
        inputTab.setContent(inputPane);
        resultsTab.setContent(resultsPane);
        metricsTab.setContent(metricsPane);
        
        // Make tabs non-closable
        inputTab.setClosable(false);
        resultsTab.setClosable(false);
        metricsTab.setClosable(false);

        tabPane.getTabs().addAll(inputTab, resultsTab, metricsTab);

        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}