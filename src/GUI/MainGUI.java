package GUI;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import main.Process;
import main.Scheduler;
import schedulers.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainGUI extends Application {


    private TextArea outputArea;
    private TextField arrivalInput, burstInput, numInput;
    private TextField rrQuantumInput;
    private TextField[] mlfqQuantumInputs = new TextField[4];
    private TextField[] mlfqAllotmentInputs = new TextField[4];
    private VBox rrBox, mlfqBox;
    private ComboBox<String> algorithmChoice;
    private ObservableList<Process> processes = FXCollections.observableArrayList();
    private TableView<Process> processTable;
    private Button addManualBtn, generateBtn, runBtn, clearBtn, exportBtn, stepBtn, playBtn, pauseBtn;
    private HBox animationControls;
    private ScrollPane ganttChartPane;
    private HBox ganttChart;
    private Timeline timeline;
    private int currentStep = 0;
    private List<Process> scheduledProcesses;
    private BarChart<String, Number> metricsChart;
    private LineChart<Number, Number> timelineChart;
    private boolean isPlaying = false;

     private TabPane tabPane;
    private Tab resultsTab;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CPU Scheduling Simulator");

         tabPane = new TabPane();
    
         Tab inputTab = new Tab("Input", createInputPane());
         inputTab.setClosable(false);
    
         resultsTab = new Tab("Results", createResultsPane());
         resultsTab.setClosable(false);
    
         Tab metricsTab = new Tab("Metrics", createMetricsPane());
         metricsTab.setClosable(false);
    
         tabPane.getTabs().addAll(inputTab, resultsTab, metricsTab);

        Scene scene = new Scene(tabPane, 1200, 800);
	scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputPane() {
        VBox inputPane = new VBox(15);
        inputPane.setPadding(new Insets(15));

        // Algorithm selection
        HBox algoSelectionBox = new HBox(15);
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
        algoSelectionBox.getChildren().addAll(new Label("Algorithm:"), algorithmChoice);

        // Process count input
        HBox numBox = new HBox(15);
        numInput = new TextField();
        numInput.setPromptText("Number of Processes");
        numInput.setPrefWidth(120);
        numBox.getChildren().addAll(new Label("Number of Processes:"), numInput);

        // Manual input fields
        HBox manualBox = new HBox(15);
        arrivalInput = new TextField();
        arrivalInput.setPromptText("Arrival Time");
        arrivalInput.setPrefWidth(120);

        burstInput = new TextField();
        burstInput.setPromptText("Burst Time");
        burstInput.setPrefWidth(120);

        addManualBtn = new Button("Add Process");
        addManualBtn.setOnAction(e -> addManualProcess());
        manualBox.getChildren().addAll(new Label("Manual Input:"), arrivalInput, burstInput, addManualBtn);

        // Quantum inputs
        rrQuantumInput = new TextField("4");
        rrQuantumInput.setPromptText("Quantum");
        rrQuantumInput.setPrefWidth(120);
        rrQuantumInput.setMaxWidth(120);
        rrBox = new VBox(5, new Label("Round Robin Quantum:"), rrQuantumInput);
        rrBox.setVisible(false);

        // MLFQ inputs
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
        mlfqBox = new VBox(5, new Label("MLFQ Parameters:"), mlfqGrid);
        mlfqBox.setVisible(false);

        // Process Table
        processTable = new TableView<>();
        processTable.setItems(processes);
        processTable.setPrefHeight(200);

        TableColumn<Process, String> pidCol = new TableColumn<>("Process ID");
        pidCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getPid()));

        TableColumn<Process, Integer> atCol = new TableColumn<>("Arrival Time");
        atCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getArrivalTime()).asObject());

        TableColumn<Process, Integer> btCol = new TableColumn<>("Burst Time");
        btCol.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getBurstTime()).asObject());

        processTable.getColumns().addAll(pidCol, atCol, btCol);

        // Buttons
        HBox buttonBox = new HBox(15);
        generateBtn = new Button("Generate Random");
        generateBtn.setOnAction(e -> generateRandomProcesses());

        runBtn = new Button("Run Scheduler");
        runBtn.setOnAction(e -> runScheduler());

        clearBtn = new Button("Reset");
        clearBtn.setOnAction(e -> clearAll());
        clearBtn.setDisable(true);

        exportBtn = new Button("Export Results");
        exportBtn.setOnAction(e -> exportResults());
        exportBtn.setDisable(true);

        buttonBox.getChildren().addAll(generateBtn, runBtn, clearBtn, exportBtn);

        inputPane.getChildren().addAll(
            algoSelectionBox,
            numBox,
            manualBox,
            rrBox,
            mlfqBox,
            processTable,
            buttonBox
        );

        return inputPane;
    }

    private VBox createResultsPane() {
        VBox resultsPane = new VBox(15);
        resultsPane.setPadding(new Insets(15));

        // Output Area
        outputArea = new TextArea();
        outputArea.setEditable(true);
        outputArea.setPrefHeight(300);
        outputArea.setPrefWidth(1000);
        outputArea.setWrapText(true);

        // Gantt Chart
        ganttChart = new HBox(2);
        ganttChart.setAlignment(Pos.CENTER_LEFT);
        ganttChartPane = new ScrollPane(ganttChart);
        ganttChartPane.setFitToHeight(true);
        ganttChartPane.setPrefHeight(150);
        ganttChartPane.setStyle("-fx-background-color: #f0f0f0;");

        // Animation Controls
        animationControls = new HBox(10);
        stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> stepAnimation());
        stepBtn.setDisable(true);

        playBtn = new Button("Play");
        playBtn.setOnAction(e -> playAnimation());
        playBtn.setDisable(true);

        pauseBtn = new Button("Pause");
        pauseBtn.setOnAction(e -> pauseAnimation());
        pauseBtn.setDisable(true);

        animationControls.getChildren().addAll(stepBtn, playBtn, pauseBtn);
        animationControls.setAlignment(Pos.CENTER);

        resultsPane.getChildren().addAll(
            new Label("Execution Details:"),
            outputArea,
            new Label("Gantt Chart Visualization:"),
            ganttChartPane,
            animationControls
        );

        return resultsPane;
    }

    private VBox createMetricsPane() {
        VBox metricsPane = new VBox(15);
        metricsPane.setPadding(new Insets(15));

        // Metrics Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        metricsChart = new BarChart<>(xAxis, yAxis);
        metricsChart.setTitle("Process Metrics");
        metricsChart.setLegendVisible(true);
        metricsChart.setPrefHeight(300);

        // Timeline Chart
        NumberAxis timelineXAxis = new NumberAxis();
        NumberAxis timelineYAxis = new NumberAxis();
        timelineChart = new LineChart<>(timelineXAxis, timelineYAxis);
        timelineChart.setTitle("Process Execution Timeline");
        timelineChart.setCreateSymbols(true);
        timelineChart.setPrefHeight(300);

        metricsPane.getChildren().addAll(
            new Label("Performance Metrics:"),
            metricsChart,
            new Label("Execution Timeline:"),
            timelineChart
        );

        return metricsPane;
    }

    private void toggleQuantumField() {
        String algo = algorithmChoice.getValue();
        rrBox.setVisible(algo != null && algo.equals("Round Robin (RR)"));
        mlfqBox.setVisible(algo != null && algo.equals("Multilevel Feedback Queue(MLFQ)"));
    }

    private void addManualProcess() {
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

    private void generateRandomProcesses() {
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

    private void runScheduler() {
        if (processes.isEmpty()) {
            showAlert("No Processes", "Please add processes before running the scheduler.");
            return;
        }

        Scheduler scheduler;
        String algo = algorithmChoice.getValue();

        try {
            switch (algo) {
                case "First Come First Serve (FCFS)" -> scheduler = new FCFS();
                case "Shortest Job First(SJF) Non-Preemptive" -> scheduler = new SJF();
                case "Shortest Remaining Time(SRTF) Preemptive" -> scheduler = new SRTF();
                case "Round Robin (RR)" -> {
                    int quantum = Integer.parseInt(rrQuantumInput.getText());
                    scheduler = new RoundRobin(quantum);
                }
                case "Multilevel Feedback Queue(MLFQ)" -> {
                    int[] tq = new int[4];
                    int[] at = new int[4];
                    for (int i = 0; i < 4; i++) {
                        tq[i] = Integer.parseInt(mlfqQuantumInputs[i].getText());
                        at[i] = Integer.parseInt(mlfqAllotmentInputs[i].getText());
                    }
                    scheduler = new MLFQ(tq, at);
                }
                default -> {
                    showAlert("Invalid Algorithm", "Please select a valid scheduling algorithm.");
                    return;
                }
            }

            scheduledProcesses = scheduler.schedule(new ArrayList<>(processes));
            displayResults(scheduledProcesses);
            updateMetricsCharts(scheduledProcesses);
            createGanttChart(scheduledProcesses);
            setupAnimation(scheduledProcesses);

            // Enable controls
            exportBtn.setDisable(false);
            stepBtn.setDisable(false);
            playBtn.setDisable(false);
            clearBtn.setDisable(false);

             tabPane.getSelectionModel().select(resultsTab);

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please check all input fields for valid numbers.");
        } catch (Exception e) {
            showAlert("Error", "An error occurred during scheduling: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayResults(List<Process> scheduled) {
        outputArea.clear();
        
        // Detailed step-by-step output
        outputArea.appendText("=== Scheduling Steps ===\n");
        int currentTime = 0;
        Process currentProcess = null;
        
        for (Process p : scheduled) {
            if (currentProcess == null || !currentProcess.equals(p)) {
                if (currentProcess != null) {
                    outputArea.appendText(String.format("At time %d: %s completes\n", currentTime, currentProcess.getPid()));
                }
                outputArea.appendText(String.format("At time %d: %s starts\n", p.getStartTime(), p.getPid()));
                currentProcess = p;
            }
            currentTime = p.getCompletionTime();
        }
        if (currentProcess != null) {
            outputArea.appendText(String.format("At time %d: %s completes\n", currentTime, currentProcess.getPid()));
        }
        
        // Metrics output
        outputArea.appendText("\n=== Process Metrics ===\n");
        outputArea.appendText("Process\tAT\tBT\tST\tCT\tTAT\tWT\tRT\n");
        
        double totalTAT = 0, totalRT = 0, totalWT = 0;
        for (Process p : scheduled) {
            int at = p.getArrivalTime();
            int bt = p.getBurstTime();
            int st = p.getStartTime();
            int ct = p.getCompletionTime();
            int tat = ct - at;
            int wt = tat - bt;
            int rt = st - at;
            totalTAT += tat;
            totalWT += wt;
            totalRT += rt;

            outputArea.appendText(String.format("%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\n", 
                p.getPid(), at, bt, st, ct, tat, wt, rt));
        }

        int n = scheduled.size();
        outputArea.appendText(String.format("\nAverage Turnaround Time: %.2f\n", totalTAT / n));
        outputArea.appendText(String.format("Average Waiting Time: %.2f\n", totalWT / n));
        outputArea.appendText(String.format("Average Response Time: %.2f\n", totalRT / n));
    }

    private void updateMetricsCharts(List<Process> scheduled) {
        metricsChart.getData().clear();
        timelineChart.getData().clear();

        // Prepare data for metrics chart
        XYChart.Series<String, Number> tatSeries = new XYChart.Series<>();
        tatSeries.setName("Turnaround Time");
        XYChart.Series<String, Number> wtSeries = new XYChart.Series<>();
        wtSeries.setName("Waiting Time");
        XYChart.Series<String, Number> rtSeries = new XYChart.Series<>();
        rtSeries.setName("Response Time");

        for (Process p : scheduled) {
            String pid = p.getPid();
            int tat = p.getCompletionTime() - p.getArrivalTime();
            int wt = tat - p.getBurstTime();
            int rt = p.getStartTime() - p.getArrivalTime();
            
            tatSeries.getData().add(new XYChart.Data<>(pid, tat));
            wtSeries.getData().add(new XYChart.Data<>(pid, wt));
            rtSeries.getData().add(new XYChart.Data<>(pid, rt));
        }

        metricsChart.getData().addAll(tatSeries, wtSeries, rtSeries);

        // Prepare data for timeline chart
        Map<String, XYChart.Series<Number, Number>> processSeries = new HashMap<>();
        for (Process p : scheduled) {
            String pid = p.getPid();
            if (!processSeries.containsKey(pid)) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(pid);
                processSeries.put(pid, series);
            }
            
            XYChart.Series<Number, Number> series = processSeries.get(pid);
            series.getData().add(new XYChart.Data<>(p.getStartTime(), scheduled.indexOf(p)));
            series.getData().add(new XYChart.Data<>(p.getCompletionTime(), scheduled.indexOf(p)));
        }

        timelineChart.getData().addAll(processSeries.values());
    }

    private void createGanttChart(List<Process> scheduled) {
        ganttChart.getChildren().clear();
        
        if (scheduled.isEmpty()) return;
        
        int maxTime = scheduled.stream()
                .mapToInt(Process::getCompletionTime)
                .max()
                .orElse(0);
        
        // Calculate scale factor to fit the chart
        double scale = 800.0 / (maxTime + 1);
        
        Process prevProcess = null;
        for (Process p : scheduled) {
            double width = (p.getCompletionTime() - p.getStartTime()) * scale;
            Rectangle rect = new Rectangle(width, 30, getProcessColor(p.getPid()));
            rect.setStroke(Color.BLACK);
            
            VBox box = new VBox();
            box.getChildren().add(new Text(p.getPid()));
            box.getChildren().add(rect);
            box.setAlignment(Pos.CENTER);
            
            ganttChart.getChildren().add(box);
            prevProcess = p;
        }
        
        // Add time markers
        HBox timeMarkers = new HBox();
        timeMarkers.setAlignment(Pos.CENTER_LEFT);
        for (int t = 0; t <= maxTime; t++) {
            Text marker = new Text(String.valueOf(t));
            HBox.setMargin(marker, new Insets(0, 0, 0, t == 0 ? 0 : scale - 10));
            timeMarkers.getChildren().add(marker);
        }
        
        VBox chartContainer = new VBox(10, ganttChart, timeMarkers);
        ganttChartPane.setContent(chartContainer);
    }

    private Color getProcessColor(String pid) {
        int hash = pid.hashCode();
        return Color.hsb(hash % 360, 0.7, 0.9);
    }

    private void setupAnimation(List<Process> scheduled) {
        if (timeline != null) {
            timeline.stop();
        }
        
        currentStep = 0;
        timeline = new Timeline();
        timeline.setCycleCount(1);
        
        int maxTime = scheduled.stream()
                .mapToInt(Process::getCompletionTime)
                .max()
                .orElse(0);
        
        for (int t = 0; t <= maxTime; t++) {
            final int time = t;
            timeline.getKeyFrames().add(new KeyFrame(
                Duration.seconds(time * 0.5), 
                e -> updateAnimation(time, scheduled)
            ));
        }
        
        timeline.setOnFinished(e -> {
            isPlaying = false;
            playBtn.setDisable(false);
            pauseBtn.setDisable(true);
        });
    }

    private void updateAnimation(int time, List<Process> scheduled) {
        outputArea.appendText(String.format("At time %d: ", time));
        
        Optional<Process> activeProcess = scheduled.stream()
            .filter(p -> p.getStartTime() <= time && p.getCompletionTime() > time)
            .findFirst();
        
        if (activeProcess.isPresent()) {
            outputArea.appendText(activeProcess.get().getPid() + " is running\n");
            highlightGanttBar(activeProcess.get().getPid());
        } else {
            outputArea.appendText("CPU is idle\n");
        }
        
        currentStep = time;
    }

    private void highlightGanttBar(String pid) {
        for (javafx.scene.Node node : ganttChart.getChildren()) {
            if (node instanceof VBox) {
                VBox box = (VBox) node;
                if (box.getChildren().get(0) instanceof Text) {
                    Text text = (Text) box.getChildren().get(0);
                    if (text.getText().equals(pid)) {
                        Rectangle rect = (Rectangle) box.getChildren().get(1);
                        rect.setFill(getProcessColor(pid).brighter());
                    } else {
                        Rectangle rect = (Rectangle) box.getChildren().get(1);
                        rect.setFill(getProcessColor(text.getText()));
                    }
                }
            }
        }
    }

    private void stepAnimation() {
        if (currentStep < timeline.getKeyFrames().size() - 1) {
            currentStep++;
            timeline.jumpTo(timeline.getKeyFrames().get(currentStep).getTime());
            updateAnimation(currentStep, scheduledProcesses);
        }
    }

    private void playAnimation() {
        if (!isPlaying) {
            isPlaying = true;
            playBtn.setDisable(true);
            pauseBtn.setDisable(false);
            timeline.play();
        }
    }

    private void pauseAnimation() {
        if (isPlaying) {
            isPlaying = false;
            timeline.pause();
            playBtn.setDisable(false);
            pauseBtn.setDisable(true);
        }
    }

    private void exportResults() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Results");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("CPU Scheduling Results");
                    writer.println("Algorithm: " + algorithmChoice.getValue());
                    writer.println("\nProcess Details:");
                    writer.println("PID\tArrival\tBurst\tStart\tCompletion\tTAT\tWT\tRT");
                    
                    for (Process p : scheduledProcesses) {
                        int tat = p.getCompletionTime() - p.getArrivalTime();
                        int wt = tat - p.getBurstTime();
                        int rt = p.getStartTime() - p.getArrivalTime();
                        
                        writer.printf("%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d%n",
                            p.getPid(), p.getArrivalTime(), p.getBurstTime(),
                            p.getStartTime(), p.getCompletionTime(), tat, wt, rt);
                    }
                    
                    double avgTAT = scheduledProcesses.stream()
                        .mapToInt(p -> p.getCompletionTime() - p.getArrivalTime())
                        .average().orElse(0);
                    double avgWT = scheduledProcesses.stream()
                        .mapToInt(p -> (p.getCompletionTime() - p.getArrivalTime()) - p.getBurstTime())
                        .average().orElse(0);
                    double avgRT = scheduledProcesses.stream()
                        .mapToInt(p -> p.getStartTime() - p.getArrivalTime())
                        .average().orElse(0);
                    
                    writer.printf("%nAverage Turnaround Time: %.2f%n", avgTAT);
                    writer.printf("Average Waiting Time: %.2f%n", avgWT);
                    writer.printf("Average Response Time: %.2f%n", avgRT);
                    
                    writer.println("\nGantt Chart:");
                    scheduledProcesses.forEach(p -> writer.print("| " + p.getPid() + " "));
                    writer.println("|");
                }
            }
        } catch (Exception e) {
            showAlert("Export Failed", "Error saving file: " + e.getMessage());
        }
    }

    private void clearAll() {
        processes.clear();
        arrivalInput.clear();
        burstInput.clear();
        numInput.clear();
        rrQuantumInput.clear();
        outputArea.clear();
        ganttChart.getChildren().clear();
        metricsChart.getData().clear();
        timelineChart.getData().clear();
        
        for (int i = 0; i < 4; i++) {
            mlfqQuantumInputs[i].clear();
            mlfqAllotmentInputs[i].clear();
        }
        
        if (timeline != null) {
            timeline.stop();
        }
        
        exportBtn.setDisable(true);
        stepBtn.setDisable(true);
        playBtn.setDisable(true);
        pauseBtn.setDisable(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}