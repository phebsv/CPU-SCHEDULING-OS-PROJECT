package GUI;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.Process;
import main.Scheduler;
import schedulers.*;

import java.util.*;

public class MainGUI extends Application {

    // Declare all UI components as class fields
    private TextArea outputArea;
    private TextField arrivalInput, burstInput, numInput;
    private TextField rrQuantumInput;
    private TextField[] mlfqQuantumInputs = new TextField[4];
    private TextField[] mlfqAllotmentInputs = new TextField[4];
    private VBox rrBox, mlfqBox;
    private ComboBox<String> algorithmChoice;
    private ObservableList<Process> processes = FXCollections.observableArrayList();
    private TableView<Process> processTable;
    private Button addManualBtn, generateBtn, runBtn, clearBtn;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CPU Scheduling Simulator");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        numInput = new TextField();
        numInput.setPromptText("e.g., 5");
        HBox numBox = new HBox(10, new Label("Number of Processes:"), numInput);
        root.getChildren().add(numBox);

        algorithmChoice = new ComboBox<>();
        algorithmChoice.getItems().addAll(
            "First Come First Serve (FCFS)", 
            "Shortest Job First(SJF) Non-Preemptive", 
            "Shortest Remaining Time(SRTF) Preemptive", 
            "Round Robin (RR)", 
            "Multilevel Feedback Queue(MLFQ)"
        );
        algorithmChoice.setValue("SELECT AN ALGORITHM");
        algorithmChoice.setOnAction(e -> toggleQuantumField());
        root.getChildren().add(new HBox(10, new Label("Algorithm:"), algorithmChoice));

        // Initialize Round Robin components
        rrQuantumInput = new TextField();
        rrQuantumInput.setPromptText("Quantum (e.g., 4)");
        rrQuantumInput.setPrefWidth(120);
        rrBox = new VBox(5, new Label("Time Quantum (Round Robin):"), rrQuantumInput);
        rrBox.setVisible(false);

        // Initialize MLFQ components
        GridPane mlfqGrid = new GridPane();
        mlfqGrid.setHgap(10);
        mlfqGrid.setVgap(5);
        for (int i = 0; i < 4; i++) {
            mlfqQuantumInputs[i] = new TextField();
            mlfqQuantumInputs[i].setPromptText("Q" + i + " Quantum");

            mlfqAllotmentInputs[i] = new TextField();
            mlfqAllotmentInputs[i].setPromptText("Q" + i + " Allotment");

            mlfqGrid.add(new Label("Q" + i + " Quantum:"), 0, i);
            mlfqGrid.add(mlfqQuantumInputs[i], 1, i);
            mlfqGrid.add(new Label("Allotment:"), 2, i);
            mlfqGrid.add(mlfqAllotmentInputs[i], 3, i);
        }
        mlfqBox = new VBox(5, new Label("MLFQ Quantum & Allotments:"), mlfqGrid);
        mlfqBox.setVisible(false);

        root.getChildren().addAll(rrBox, mlfqBox);

        arrivalInput = new TextField();
        arrivalInput.setPromptText("Arrival Time");

        burstInput = new TextField();
        burstInput.setPromptText("Burst Time");

        addManualBtn = new Button("Add Process");
        addManualBtn.setOnAction(e -> addManualProcess());

        HBox manualBox = new HBox(10, new Label("Manual Input:"), arrivalInput, burstInput, addManualBtn);
        root.getChildren().add(manualBox);

        processTable = new TableView<>();
        processTable.setItems(processes);
        processTable.setPrefHeight(200);
        
        TableColumn<Process, String> pidCol = new TableColumn<>("PID");
        pidCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPid()));
        
        TableColumn<Process, Integer> atCol = new TableColumn<>("Arrival");
        atCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArrivalTime()).asObject());
        
        TableColumn<Process, Integer> btCol = new TableColumn<>("Burst");
        btCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBurstTime()).asObject());
        
        @SuppressWarnings("unchecked")
        TableColumn<Process, ?>[] columns = new TableColumn[] {pidCol, atCol, btCol};
        processTable.getColumns().addAll(columns);
        root.getChildren().add(processTable);

        generateBtn = new Button("Generate Random");
        generateBtn.setOnAction(e -> generateRandomProcesses());

        runBtn = new Button("Run Scheduler");
        runBtn.setOnAction(e -> runScheduler());

        clearBtn = new Button("Reset");
        clearBtn.setOnAction(e -> clearAll());
        clearBtn.setDisable(true);

        HBox buttonBox = new HBox(10, generateBtn, runBtn, clearBtn);
        root.getChildren().add(buttonBox);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);
        root.getChildren().add(outputArea);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        toggleQuantumField();
    }

    private void toggleQuantumField() {
        String algo = algorithmChoice.getValue();
        if (algo == null) return;
        
        rrBox.setVisible(algo.equals("Round Robin (RR)"));
        mlfqBox.setVisible(algo.equals("Multilevel Feedback Queue(MLFQ)"));
    }

    private void addManualProcess() {
        try {
            int at = Integer.parseInt(arrivalInput.getText());
            int bt = Integer.parseInt(burstInput.getText());
            String pid = "P" + (processes.size() + 1);
            Process p = new Process(pid, at, bt);
            processes.add(p);
            arrivalInput.clear();
            burstInput.clear();
        } catch (NumberFormatException e) {
            outputArea.appendText("Invalid input for arrival or burst time.\n");
        }
    }

    private void generateRandomProcesses() {
        processes.clear();
        outputArea.clear();
        int n;
        try {
            n = Integer.parseInt(numInput.getText());
        } catch (Exception e) {
            outputArea.setText("Invalid number of processes.\n");
            return;
        }

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int at = rand.nextInt(10);
            int bt = rand.nextInt(5) + 1;
            processes.add(new Process("P" + (i + 1), at, bt));
        }
    }

    private void runScheduler() {
        if (processes.isEmpty()) {
            outputArea.appendText("No processes to schedule.\n");
            return;
        }

        String algo = algorithmChoice.getValue();
        Scheduler scheduler;

        switch (algo) {
            case "First Come First Serve (FCFS)" -> scheduler = new FCFS();
            case "Shortest Job First(SJF) Non-Preemptive" -> scheduler = new SJF();
            case "Shortest Remaining Time(SRTF) Preemptive" -> scheduler = new SRTF();
            case "Round Robin (RR)" -> {
                int q;
                try {
                    q = Integer.parseInt(rrQuantumInput.getText());
                } catch (Exception e) {
                    outputArea.appendText("Invalid quantum value.\n");
                    return;
                }
                scheduler = new RoundRobin(q);
            }
            case "Multilevel Feedback Queue(MLFQ)" -> {
                int[] tq = new int[4];
                int[] at = new int[4];
                try {
                    for (int i = 0; i < 4; i++) {
                        tq[i] = Integer.parseInt(mlfqQuantumInputs[i].getText());
                        at[i] = Integer.parseInt(mlfqAllotmentInputs[i].getText());
                    }
                } catch (Exception e) {
                    outputArea.appendText("Invalid MLFQ parameters.\n");
                    return;
                }
                scheduler = new MLFQ(tq, at);
            }
            default -> {
                outputArea.appendText("Unknown Algorithm.\n");
                return;
            }
        }

        List<Process> scheduled = scheduler.schedule(new ArrayList<>(processes));
        displayResults(scheduled);

        // Disable all except reset
        arrivalInput.setDisable(true);
        burstInput.setDisable(true);
        numInput.setDisable(true);
        algorithmChoice.setDisable(true);
        addManualBtn.setDisable(true);
        generateBtn.setDisable(true);
        runBtn.setDisable(true);
        clearBtn.setDisable(false);
    }

    private void displayResults(List<Process> scheduled) {
        outputArea.appendText("\nGantt Chart:\n");
        for (Process p : scheduled) outputArea.appendText("| " + p.getPid() + " ");
        outputArea.appendText("|\n");

        int time = scheduled.get(0).getStartTime();
        outputArea.appendText(String.valueOf(time));
        for (Process p : scheduled) {
            time = p.getCompletionTime();
            outputArea.appendText("   " + time);
        }

        outputArea.appendText("\n\nProcess\tAT\tBT\tST\tCT\tTAT\tRT\n");
        double totalTAT = 0, totalRT = 0;

        for (Process p : scheduled) {
            int at = p.getArrivalTime();
            int bt = p.getBurstTime();
            int st = p.getStartTime();
            int ct = p.getCompletionTime();
            int tat = ct - at;
            int rt = st - at;

            totalTAT += tat;
            totalRT += rt;

            outputArea.appendText(p.getPid() + "\t" + at + "\t" + bt + "\t" +
                    st + "\t" + ct + "\t" + tat + "\t" + rt + "\n");
        }

        int n = scheduled.size();
        outputArea.appendText(String.format("\nAverage Turnaround Time: %.2f\n", totalTAT / n));
        outputArea.appendText(String.format("Average Response Time: %.2f\n", totalRT / n));
    }

    private void clearAll() {
        processes.clear();
        arrivalInput.clear();
        burstInput.clear();
        numInput.clear();
        rrQuantumInput.clear();
        for (int i = 0; i < 4; i++) {
            mlfqQuantumInputs[i].clear();
            mlfqAllotmentInputs[i].clear();
        }
        outputArea.clear();

        arrivalInput.setDisable(false);
        burstInput.setDisable(false);
        numInput.setDisable(false);
        algorithmChoice.setDisable(false);
        addManualBtn.setDisable(false);
        generateBtn.setDisable(false);
        runBtn.setDisable(false);

        toggleQuantumField();
        clearBtn.setDisable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}