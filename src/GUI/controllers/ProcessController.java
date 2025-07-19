package GUI.controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
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
    private List<Process> lastScheduledProcesses;
    private Scheduler scheduler;

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
        
        rrBox.setVisible(showRR);
        mlfqBox.setVisible(showMLFQ);
        
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
            scheduler = createScheduler();
            if (scheduler == null) return;

            lastScheduledProcesses = scheduler.schedule(new ArrayList<>(processes));
            displayResults(lastScheduledProcesses);
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
        
        if (algorithmChoice.getValue().equals("Round Robin (RR)") && 
            scheduler instanceof RoundRobin rr) {
            resultsPane.createRRGanttChart(rr.getGanttEntries());
        } else if (algorithmChoice.getValue().equals("Shortest Remaining Time(SRTF) Preemptive")) {
            resultsPane.createSRTFGanttChart(scheduledProcesses);
        } else {
            resultsPane.createGanttChart(scheduledProcesses);
        }
        
        resultsPane.setupAnimation(scheduledProcesses);
        resultsPane.enableControls(true);
    }
    
    if (metricsPane != null) {
        metricsPane.updateMetricsCharts(scheduledProcesses);
    }
}

    public void clearAll(TextField... fields) {
        processes.clear();
        lastScheduledProcesses = null;
        for (TextField field : fields) {
            if (field != null) {
                field.clear();
            }
        }
        
        if (resultsPane != null) {
            resultsPane.enableControls(false);
        }
        if (metricsPane != null) {
            metricsPane.clearCharts();
        }
    }

    public void exportResults() {
        if (lastScheduledProcesses == null || lastScheduledProcesses.isEmpty()) {
            showAlert("No Results", "Please run the scheduler first to generate results.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Scheduling Results");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setInitialFileName("cpu_scheduling_results_" + 
            System.currentTimeMillis() + ".txt");

        Stage stage = (Stage) tabPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("CPU SCHEDULING RESULTS");
                writer.println("======================");
                writer.println("Algorithm: " + algorithmChoice.getValue());
                writer.println("Generated at: " + new Date());
                writer.println();

                writer.println("PROCESS DETAILS");
                writer.println("PID\tArrival\tBurst");
                for (Process p : processes) {
                    writer.printf("%s\t%d\t%d%n",
                        p.getPid(),
                        p.getArrivalTime(),
                        p.getBurstTime());
                }
                writer.println();

                writer.println("EXECUTION TIMELINE");
                if (algorithmChoice.getValue().equals("Round Robin (RR)") && 
                    scheduler instanceof RoundRobin rr) {
                    for (RoundRobin.GanttEntry entry : rr.getGanttEntries()) {
                        writer.printf("[%d-%d]\t%s%n", 
                            entry.startTime, 
                            entry.endTime, 
                            entry.pid);
                    }
                } else {
                    for (Process p : lastScheduledProcesses) {
                        writer.printf("[%d-%d]\t%s%n", 
                            p.getStartTime(), 
                            p.getCompletionTime(), 
                            p.getPid());
                    }
                }
                writer.println();

                writer.println("PERFORMANCE METRICS");
                writer.printf("Total Processes: %d%n", processes.size());
                writer.printf("Total Execution Time: %d units%n", 
                    lastScheduledProcesses.stream()
                        .mapToInt(Process::getCompletionTime)
                        .max()
                        .orElse(0));

                showAlert("Export Successful", 
                    "Results successfully exported to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Export Failed", 
                    "Error exporting results:\n" + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}