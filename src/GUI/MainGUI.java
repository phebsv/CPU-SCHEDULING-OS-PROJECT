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

    private TextArea outputArea;
    private TextField arrivalInput, burstInput, numInput, quantumInput;
    private ComboBox<String> algorithmChoice;
    private ObservableList<Process> processes = FXCollections.observableArrayList();
    private TableView<Process> processTable;

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
        algorithmChoice.getItems().addAll("FCFS", "SJF", "SRTF", "Round Robin", "MLFQ");
        algorithmChoice.setValue("FCFS");
        algorithmChoice.setOnAction(e -> toggleQuantumField());
        root.getChildren().add(new HBox(10, new Label("Algorithm:"), algorithmChoice));

        quantumInput = new TextField();
        quantumInput.setPromptText("Enter Quantum");
        HBox quantumBox = new HBox(10, new Label("Quantum (RR/MLFQ):"), quantumInput);
        root.getChildren().add(quantumBox);

        // Manual input fields
        arrivalInput = new TextField();
        arrivalInput.setPromptText("Arrival Time");

        burstInput = new TextField();
        burstInput.setPromptText("Burst Time");

        Button addManualBtn = new Button("Add Process");
        addManualBtn.setOnAction(e -> addManualProcess());

        HBox manualBox = new HBox(10, new Label("Manual Input:"), arrivalInput, burstInput, addManualBtn);
        root.getChildren().add(manualBox);

        // TableView for processes
        processTable = new TableView<>();
        processTable.setItems(processes);
        processTable.setPrefHeight(200);
        TableColumn<Process, String> pidCol = new TableColumn<>("PID");
        pidCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPid()));
        TableColumn<Process, Integer> atCol = new TableColumn<>("Arrival");
        atCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArrivalTime()).asObject());
        TableColumn<Process, Integer> btCol = new TableColumn<>("Burst");
        btCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBurstTime()).asObject());
        processTable.getColumns().addAll(pidCol, atCol, btCol);
        root.getChildren().add(processTable);

        Button generateBtn = new Button("Generate Random");
        generateBtn.setOnAction(e -> generateRandomProcesses());

        Button runBtn = new Button("Run Scheduler");
        runBtn.setOnAction(e -> runScheduler());

        Button clearBtn = new Button("Clear All");
        clearBtn.setOnAction(e -> clearAll());

        HBox buttonBox = new HBox(10, generateBtn, runBtn, clearBtn);
        root.getChildren().add(buttonBox);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);
        root.getChildren().add(outputArea);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        toggleQuantumField(); // initial state
    }

    private void toggleQuantumField() {
        String algo = algorithmChoice.getValue();
        boolean showQuantum = algo.equals("Round Robin") || algo.equals("MLFQ");
        quantumInput.setDisable(!showQuantum);
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
            case "FCFS" -> scheduler = new FCFS();
            case "SJF" -> scheduler = new SJF();
            case "SRTF" -> scheduler = new SRTF();
            case "Round Robin" -> {
                int q;
                try {
                    q = Integer.parseInt(quantumInput.getText());
                } catch (Exception e) {
                    outputArea.appendText("Invalid quantum value.\n");
                    return;
                }
                scheduler = new RoundRobin(q);
            }
            case "MLFQ" -> {
                int[] tq = {2, 4, 8};
                int[] at = {4, 8, 16};
                scheduler = new MLFQ(tq, at);
            }
            default -> {
                outputArea.appendText("Unknown Algorithm.\n");
                return;
            }
        }

        List<Process> scheduled = scheduler.schedule(new ArrayList<>(processes));
        displayResults(scheduled);
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
            int tat = p.getTurnaroundTime();
            int rt = p.getResponseTime();
            totalTAT += tat;
            totalRT += rt;
            outputArea.appendText(p.getPid() + "\t" + p.getArrivalTime() + "\t" + p.getBurstTime() + "\t" +
                    p.getStartTime() + "\t" + p.getCompletionTime() + "\t" + tat + "\t" + rt + "\n");
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
        quantumInput.clear();
        outputArea.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
