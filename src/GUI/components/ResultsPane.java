package GUI.components;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.paint.*;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;
import main.Process;

public class ResultsPane extends VBox {
    private TextArea outputArea;
    private ScrollPane ganttChartPane;
    private HBox ganttChart;
    private HBox animationControls;
    private Timeline timeline;
    private int currentStep = 0;
    private List<Process> scheduledProcesses;
    private boolean isPlaying = false;
    private Button stepBtn, playBtn, pauseBtn;

    public ResultsPane() {
        super(15);
        setPadding(new Insets(15));
        createUI();
    }

    private void createUI() {
        // Output Area
        outputArea = new TextArea();
        outputArea.setEditable(false);
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

        getChildren().addAll(
            new Label("Execution Details:"),
            outputArea,
            new Label("Gantt Chart Visualization:"),
            ganttChartPane,
            animationControls
        );
    }

    public void displayResults(List<Process> scheduled) {
        this.scheduledProcesses = scheduled;
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

    public void createGanttChart(List<Process> scheduled) {
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

    public void setupAnimation(List<Process> scheduled) {
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

        stepBtn.setDisable(false);
        playBtn.setDisable(false);
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

    public void enableControls(boolean enabled) {
        stepBtn.setDisable(!enabled);
        playBtn.setDisable(!enabled);
        pauseBtn.setDisable(!enabled);
    }
}