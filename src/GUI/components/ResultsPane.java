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
import javafx.scene.Node;

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
    private Map<String, Color> processColors = new HashMap<>();
    private static final double BLOCK_WIDTH = 40.0;
    private static final Color[] COLOR_PALETTE = {
        Color.rgb(100, 149, 237), // Cornflower Blue
        Color.rgb(220, 20, 60),   // Crimson
        Color.rgb(46, 139, 87),    // Sea Green
        Color.rgb(255, 215, 0),    // Gold
        Color.rgb(147, 112, 219),  // Medium Purple
        Color.rgb(70, 130, 180),   // Steel Blue
        Color.rgb(255, 127, 80),   // Coral
        Color.rgb(218, 112, 214)   // Orchid
    };

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

        // Gantt Chart Container
        ganttChart = new HBox(0);
        ganttChart.setAlignment(Pos.CENTER_LEFT);
        
        // Time markers container
        HBox timeMarkers = new HBox();
        timeMarkers.setAlignment(Pos.BOTTOM_LEFT);
        
        // Main container for chart and markers
        VBox chartContainer = new VBox(5);
        chartContainer.getChildren().addAll(ganttChart, timeMarkers);
        
        ganttChartPane = new ScrollPane(chartContainer);
        ganttChartPane.setFitToHeight(true);
        ganttChartPane.setPrefHeight(150);
        ganttChartPane.setStyle("-fx-background-color: #f0f0f0;");

        // Animation Controls
        animationControls = new HBox(10);
        stepBtn = new Button("Step");
        playBtn = new Button("Play");
        pauseBtn = new Button("Pause");
        animationControls.getChildren().addAll(stepBtn, playBtn, pauseBtn);
        animationControls.setAlignment(Pos.CENTER);

        getChildren().addAll(
            new Label("Execution Details:"), outputArea,
            new Label("Gantt Chart Visualization:"), ganttChartPane,
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
        processColors.clear();
        
        if (scheduled.isEmpty()) return;
        
        // Assign colors from palette
        int colorIndex = 0;
        for (Process p : scheduled) {
            if (!processColors.containsKey(p.getPid())) {
                processColors.put(p.getPid(), COLOR_PALETTE[colorIndex % COLOR_PALETTE.length]);
                colorIndex++;
            }
        }
        
        // Create timeline blocks
        int maxTime = 0;
        int prevEndTime = 0;
        
        for (Process p : scheduled) {
            int start = p.getStartTime();
            int end = p.getCompletionTime();
            maxTime = Math.max(maxTime, end);
            
            // Add idle time if needed
            if (start > prevEndTime) {
                addIdleBlock(prevEndTime, start);
            }
            
            // Add process block
            addProcessBlock(p, start, end);
            prevEndTime = end;
        }
        
        // Add time markers
        addTimeMarkers(maxTime);
    }

    private void addProcessBlock(Process p, int start, int end) {
        double width = (end - start) * BLOCK_WIDTH;
        Rectangle rect = new Rectangle(width, 30, processColors.get(p.getPid()));
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1);
        
        VBox block = new VBox(
            new Text(p.getPid()),
            rect,
            new Text(start + "-" + end)
        );
        block.setAlignment(Pos.CENTER);
        block.setUserData(p);
        block.getStyleClass().add("gantt-block");
        
        ganttChart.getChildren().add(block);
    }

    private void addIdleBlock(int start, int end) {
        double width = (end - start) * BLOCK_WIDTH;
        Rectangle rect = new Rectangle(width, 30, Color.LIGHTGRAY);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1);
        
        VBox block = new VBox(
            new Text("IDLE"),
            rect,
            new Text(start + "-" + end)
        );
        block.setAlignment(Pos.CENTER);
        block.getStyleClass().add("gantt-idle-block");
        
        ganttChart.getChildren().add(block);
    }

    private void addTimeMarkers(int maxTime) {
        HBox timeMarkers = (HBox) ((VBox)ganttChartPane.getContent()).getChildren().get(1);
        timeMarkers.getChildren().clear();
        
        for (int t = 0; t <= maxTime; t++) {
            Text marker = new Text(String.valueOf(t));
            marker.getStyleClass().add("gantt-time-marker");
            HBox.setMargin(marker, new Insets(0, 0, 0, t == 0 ? 0 : BLOCK_WIDTH - 10));
            timeMarkers.getChildren().add(marker);
        }
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
            resetHighlighting();
        });

        stepBtn.setOnAction(e -> stepAnimation());
        playBtn.setOnAction(e -> playAnimation());
        pauseBtn.setOnAction(e -> pauseAnimation());
        
        enableControls(true);
    }

    private void updateAnimation(int time, List<Process> scheduled) {
        outputArea.appendText(String.format("At time %d: ", time));
        
        Optional<Process> activeProcess = scheduled.stream()
            .filter(p -> p.getStartTime() <= time && p.getCompletionTime() > time)
            .findFirst();
        
        resetHighlighting();
        
        if (activeProcess.isPresent()) {
            outputArea.appendText(activeProcess.get().getPid() + " is running\n");
            highlightBlock(activeProcess.get(), true);
        } else {
            outputArea.appendText("CPU is idle\n");
            highlightIdleBlock(time);
        }
        
        currentStep = time;
    }

    private void highlightBlock(Process process, boolean highlight) {
        for (Node node : ganttChart.getChildren()) {
            if (node instanceof VBox) {
                VBox block = (VBox) node;
                if (block.getUserData() != null && block.getUserData().equals(process)) {
                    Rectangle rect = (Rectangle) block.getChildren().get(1);
                    if (highlight) {
                        rect.setFill(processColors.get(process.getPid()).brighter());
                        rect.setStroke(Color.WHITE);
                        rect.setStrokeWidth(2);
                    } else {
                        rect.setFill(processColors.get(process.getPid()));
                        rect.setStroke(Color.BLACK);
                        rect.setStrokeWidth(1);
                    }
                }
            }
        }
    }

    private void highlightIdleBlock(int time) {
        for (Node node : ganttChart.getChildren()) {
            if (node instanceof VBox) {
                VBox block = (VBox) node;
                if (block.getChildren().get(0) instanceof Text && 
                    ((Text)block.getChildren().get(0)).getText().equals("IDLE")) {
                    String[] times = ((Text)block.getChildren().get(2)).getText().split("-");
                    int start = Integer.parseInt(times[0]);
                    int end = Integer.parseInt(times[1]);
                    
                    if (time >= start && time < end) {
                        Rectangle rect = (Rectangle) block.getChildren().get(1);
                        rect.setFill(Color.DARKGRAY);
                        rect.setStroke(Color.WHITE);
                        rect.setStrokeWidth(2);
                    }
                }
            }
        }
    }

    private void resetHighlighting() {
        for (Node node : ganttChart.getChildren()) {
            if (node instanceof VBox) {
                VBox block = (VBox) node;
                if (block.getUserData() != null) {
                    Process p = (Process) block.getUserData();
                    Rectangle rect = (Rectangle) block.getChildren().get(1);
                    rect.setFill(processColors.get(p.getPid()));
                    rect.setStroke(Color.BLACK);
                    rect.setStrokeWidth(1);
                } else if (block.getChildren().get(0) instanceof Text && 
                          ((Text)block.getChildren().get(0)).getText().equals("IDLE")) {
                    Rectangle rect = (Rectangle) block.getChildren().get(1);
                    rect.setFill(Color.LIGHTGRAY);
                    rect.setStroke(Color.BLACK);
                    rect.setStrokeWidth(1);
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
        pauseBtn.setDisable(!enabled || isPlaying);
    }
}