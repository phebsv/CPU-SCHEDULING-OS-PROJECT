package GUI.components;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.beans.property.*;
import main.Process;
import GUI.controllers.ProcessController;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

public class InputPane extends VBox {
    private final TextField arrivalInput;
    private final TextField burstInput;
    private final TextField numInput;
    private final TextField rrQuantumInput;
    private final TextField[] mlfqQuantumInputs;
    private final TextField[] mlfqAllotmentInputs;
    private final ComboBox<String> algorithmChoice;
    private final TableView<Process> processTable;
    private final ObservableList<Process> processes;
    private final ProcessController processController;
    private final VBox rrBox;
    private final VBox mlfqBox;

    public InputPane(TabPane tabPane, Tab resultsTab) {
        super(15);
        setPadding(new Insets(15));

        // Initialize fields
        this.processes = FXCollections.observableArrayList();
        this.algorithmChoice = new ComboBox<>();
        this.rrQuantumInput = new TextField("4");
        this.mlfqQuantumInputs = new TextField[4];
        this.mlfqAllotmentInputs = new TextField[4];
        this.arrivalInput = new TextField();
        this.burstInput = new TextField();
        this.numInput = new TextField();
        this.processTable = new TableView<>();
        
        // Initialize quantum containers
        this.rrBox = new VBox(5, new Label("Round Robin Quantum:"), rrQuantumInput);
        this.mlfqBox = new VBox(5, new Label("MLFQ Parameters:"), createMLFQGrid());

        createUI();
        
        this.processController = new ProcessController(
            processes, algorithmChoice, 
            rrQuantumInput, mlfqQuantumInputs, mlfqAllotmentInputs,
            tabPane, resultsTab, rrBox, mlfqBox
        );
    }

    private GridPane createMLFQGrid() {
        GridPane mlfqGrid = new GridPane();
        mlfqGrid.setHgap(10);
        mlfqGrid.setVgap(5);
        for (int i = 0; i < 4; i++) {
            mlfqQuantumInputs[i] = new TextField(String.valueOf((i+1)*2));
            mlfqQuantumInputs[i].setPromptText("Q" + i + " Quantum");
            mlfqQuantumInputs[i].setPrefWidth(60);

            mlfqAllotmentInputs[i] = new TextField(String.valueOf((i+1)*3));
            mlfqAllotmentInputs[i].setPromptText("Q" + i + " Allotment");
            mlfqAllotmentInputs[i].setPrefWidth(60);

            mlfqGrid.add(new Label("Q" + i + ":"), 0, i);
            mlfqGrid.add(mlfqQuantumInputs[i], 1, i);
            mlfqGrid.add(new Label("Allot:"), 2, i);
            mlfqGrid.add(mlfqAllotmentInputs[i], 3, i);
        }
        return mlfqGrid;
    }

    private void createUI() {
        // Algorithm selection
        HBox algoSelectionBox = new HBox(15);
        algorithmChoice.getItems().addAll(
            "First Come First Serve (FCFS)",
            "Shortest Job First(SJF) Non-Preemptive",
            "Shortest Remaining Time(SRTF) Preemptive",
            "Round Robin (RR)",
            "Multilevel Feedback Queue(MLFQ)"
        );
        algorithmChoice.setValue("SELECT AN ALGORITHM");
        algorithmChoice.setOnAction(e -> processController.toggleQuantumField());
        algoSelectionBox.getChildren().addAll(new Label("Algorithm:"), algorithmChoice);

        // Process count input
        HBox numBox = new HBox(15);
        numInput.setPromptText("Number of Processes");
        numInput.setPrefWidth(120);
        numBox.getChildren().addAll(new Label("Number of Processes:"), numInput);

        // Manual input fields
        HBox manualBox = new HBox(15);
        arrivalInput.setPromptText("Arrival Time");
        arrivalInput.setPrefWidth(120);
        burstInput.setPromptText("Burst Time");
        burstInput.setPrefWidth(120);

        Button addManualBtn = new Button("Add Process");
        addManualBtn.setOnAction(e -> processController.addManualProcess(arrivalInput, burstInput));
        manualBox.getChildren().addAll(new Label("Manual Input:"), arrivalInput, burstInput, addManualBtn);

        // Quantum inputs
        rrQuantumInput.setPromptText("Quantum");
        rrQuantumInput.setPrefWidth(120);
        rrQuantumInput.setMaxWidth(120);
        rrBox.setVisible(false);
        rrBox.managedProperty().bind(rrBox.visibleProperty());

        mlfqBox.setVisible(false);
        mlfqBox.managedProperty().bind(mlfqBox.visibleProperty());

        // Process Table
        processTable.setItems(processes);
        processTable.setPrefHeight(200);

        TableColumn<Process, String> pidCol = new TableColumn<>("Process ID");
        pidCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPid()));

        TableColumn<Process, Integer> atCol = new TableColumn<>("Arrival Time");
        atCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArrivalTime()).asObject());

        TableColumn<Process, Integer> btCol = new TableColumn<>("Burst Time");
        btCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBurstTime()).asObject());

        @SuppressWarnings("unchecked")
        TableColumn<Process, ?>[] columns = new TableColumn[] {pidCol, atCol, btCol};
        processTable.getColumns().addAll(columns);

        // Buttons
        HBox buttonBox = new HBox(15);
        Button generateBtn = new Button("Generate Random");
        generateBtn.setOnAction(e -> processController.generateRandomProcesses(numInput));

        Button runBtn = new Button("Run Scheduler");
        runBtn.setOnAction(e -> processController.runScheduler());

        Button clearBtn = new Button("Reset");
        clearBtn.setOnAction(e -> {
            processController.clearAll(arrivalInput, burstInput, numInput, rrQuantumInput);
            for (TextField field : mlfqQuantumInputs) {
                if (field != null) field.clear();
            }
            for (TextField field : mlfqAllotmentInputs) {
                if (field != null) field.clear();
            }
        });

        Button exportBtn = new Button("Export Results");
        exportBtn.setOnAction(e -> processController.exportResults());

        buttonBox.getChildren().addAll(generateBtn, runBtn, clearBtn, exportBtn);

        getChildren().addAll(
            algoSelectionBox,
            numBox,
            manualBox,
            rrBox,
            mlfqBox,
            processTable,
            buttonBox
        );
    }

    public void setResultsPane(ResultsPane resultsPane) {
        processController.setResultsPane(resultsPane);
    }

    public void setMetricsPane(MetricsPane metricsPane) {
        processController.setMetricsPane(metricsPane);
    }
}