package GUI.controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import main.Process;
import main.Scheduler;
import schedulers.*;
import java.util.*;
import GUI.components.*;

public class ProcessController {
    private final ObservableList<Process> processes;
    private final ComboBox<String> algorithmChoice;
    private final TextField rrQuantumInput;
    private final TextField[] mlfqQuantumInputs;
    private final TextField[] mlfqAllotmentInputs;
    private final TabPane tabPane;
    private final Tab resultsTab;
    private final VBox rrBox;
    private final VBox mlfqBox;
    private ResultsPane resultsPane;
    private MetricsPane metricsPane;

    public ProcessController(ObservableList<Process> processes, 
                          ComboBox<String> algorithmChoice,
                          TextField rrQuantumInput,
                          TextField[] mlfqQuantumInputs,
                          TextField[] mlfqAllotmentInputs,
                          TabPane tabPane,
                          Tab resultsTab,
                          VBox rrBox,
                          VBox mlfqBox) {
        this.processes = processes;
        this.algorithmChoice = algorithmChoice;
        this.rrQuantumInput = rrQuantumInput;
        this.mlfqQuantumInputs = mlfqQuantumInputs;
        this.mlfqAllotmentInputs = mlfqAllotmentInputs;
        this.tabPane = tabPane;
        this.resultsTab = resultsTab;
        this.rrBox = rrBox;
        this.mlfqBox = mlfqBox;
    }

    public void toggleQuantumField() {
        String algo = algorithmChoice.getValue();
        boolean showRR = "Round Robin (RR)".equals(algo);
        boolean showMLFQ = "Multilevel Feedback Queue(MLFQ)".equals(algo);
        
        // Set visibility for containers
        rrBox.setVisible(showRR);
        mlfqBox.setVisible(showMLFQ);
        
        // Set visibility for individual fields (in case needed)
        rrQuantumInput.setVisible(showRR);
        rrQuantumInput.setManaged(showRR);
        
        for (TextField field : mlfqQuantumInputs) {
            field.setVisible(showMLFQ);
            field.setManaged(showMLFQ);
        }
        for (TextField field : mlfqAllotmentInputs) {
            field.setVisible(showMLFQ);
            field.setManaged(showMLFQ);
        }
    }

    // ... (rest of the methods remain exactly the same as in your original file)
    // Keep all other methods unchanged
    public void setResultsPane(ResultsPane resultsPane) {
        this.resultsPane = resultsPane;
    }

    public void setMetricsPane(MetricsPane metricsPane) {
        this.metricsPane = metricsPane;
    }

    public void addManualProcess(TextField arrivalInput, TextField burstInput) {
        try {
            int at = Integer.parseInt(arrivalInput.getText());
            int bt = Integer.parseInt(burstInput.getText());
            String pid = "P" + (processes.size() + 1);
            processes.add(new Process(pid, at, bt));
            arrivalInput.clear();
            burstInput.clear();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for arrival and burst time.");
        }
    }

    public void generateRandomProcesses(TextField numInput) {
        processes.clear();
        int n;
        try {
            n = Integer.parseInt(numInput.getText());
        } catch (Exception e) {
            showAlert("Invalid Input", "Please enter a valid number of processes.");
            return;
        }

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int at = rand.nextInt(10);
            int bt = rand.nextInt(10) + 1;
            processes.add(new Process("P" + (i + 1), at, bt));
        }
    }

    public void runScheduler() {
        if (processes.isEmpty()) {
            showAlert("No Processes", "Please add processes before running the scheduler.");
            return;
        }

        try {
            Scheduler scheduler = createScheduler();
            if (scheduler == null) return;

            List<Process> scheduledProcesses = scheduler.schedule(new ArrayList<>(processes));
            displayResults(scheduledProcesses);
            tabPane.getSelectionModel().select(resultsTab);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please check all input fields for valid numbers.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred during scheduling: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Scheduler createScheduler() {
        String algo = algorithmChoice.getValue();
        switch (algo) {
            case "First Come First Serve (FCFS)":
                return new FCFS();
            case "Shortest Job First(SJF) Non-Preemptive":
                return new SJF();
            case "Shortest Remaining Time(SRTF) Preemptive":
                return new SRTF();
            case "Round Robin (RR)":
                int quantum = Integer.parseInt(rrQuantumInput.getText());
                return new RoundRobin(quantum);
            case "Multilevel Feedback Queue(MLFQ)":
                int[] tq = new int[4];
                int[] at = new int[4];
                for (int i = 0; i < 4; i++) {
                    tq[i] = Integer.parseInt(mlfqQuantumInputs[i].getText());
                    at[i] = Integer.parseInt(mlfqAllotmentInputs[i].getText());
                }
                return new MLFQ(tq, at);
            default:
                showAlert("Invalid Algorithm", "Please select a valid scheduling algorithm.");
                return null;
        }
    }

    private void displayResults(List<Process> scheduledProcesses) {
        if (resultsPane != null) {
            resultsPane.displayResults(scheduledProcesses);
            resultsPane.createGanttChart(scheduledProcesses);
            resultsPane.setupAnimation(scheduledProcesses);
            resultsPane.enableControls(true);
        }
        
        if (metricsPane != null) {
            metricsPane.updateMetricsCharts(scheduledProcesses);
        }
    }

    public void clearAll(TextField... fields) {
        processes.clear();
        for (TextField field : fields) {
            if (field != null) {
                field.clear();
            }
        }
        
        if (resultsPane != null) {
            resultsPane.enableControls(false);
        }
    }

    public void exportResults() {
        showAlert("Export", "Export functionality would be implemented here");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}