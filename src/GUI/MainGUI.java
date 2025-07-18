package GUI;

import javafx.application.Application;
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
    private List<Process> processes = new ArrayList<>();

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
        root.getChildren().add(new HBox(10, new Label("Algorithm:"), algorithmChoice));

        quantumInput = new TextField();
        quantumInput.setPromptText("Enter Quantum");
        HBox quantumBox = new HBox(10, new Label("Quantum (RR/MLFQ):"), quantumInput);
        root.getChildren().add(quantumBox);

        Button generateBtn = new Button("Generate Processes Randomly");
        generateBtn.setOnAction(e -> generateRandomProcesses());
        root.getChildren().add(generateBtn);

        Button runBtn = new Button("Run Scheduler");
        runBtn.setOnAction(e -> runScheduler());
        root.getChildren().add(runBtn);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);
        root.getChildren().add(outputArea);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
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

        outputArea.appendText("Generated Processes:\n");
        for (Process p : processes) {
            outputArea.appendText(p.getPid() + " - Arrival: " + p.getArrivalTime() + ", Burst: " + p.getBurstTime() + "\n");
        }
    }

    private void runScheduler() {
        if (processes.isEmpty()) {
            outputArea.appendText("No processes to schedule. Generate first.\n");
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
                    outputArea.appendText("\nInvalid quantum.\n");
                    return;
                }
                scheduler = new RoundRobin(q);
            }
            case "MLFQ" -> {
                int[] tq = {2, 4, 8, 16};
                int[] at = {4, 8, 16, 32};
                scheduler = new MLFQ(tq, at);
            }
            default -> {
                outputArea.appendText("\nInvalid Algorithm.\n");
                return;
            }
        }

        List<Process> scheduled = scheduler.schedule(new ArrayList<>(processes));
        displayResults(scheduled);
    }

    private void displayResults(List<Process> scheduled) {
        outputArea.appendText("\nGantt Chart:\n");
        for (Process p : scheduled) {
            outputArea.appendText("| " + p.getPid() + " ");
        }
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

    public static void main(String[] args) {
        launch(args);
    }
}
