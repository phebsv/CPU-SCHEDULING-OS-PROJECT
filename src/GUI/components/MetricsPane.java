package GUI.components;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.geometry.Insets;
import java.util.*;
import main.Process;

public class MetricsPane extends VBox {
    private BarChart<String, Number> metricsChart;
    private LineChart<Number, Number> timelineChart;

    public MetricsPane() {
        super(15);
        setPadding(new Insets(15));
        createUI();
    }

    private void createUI() {
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

        getChildren().addAll(
            new Label("Performance Metrics:"),
            metricsChart,
            new Label("Execution Timeline:"),
            timelineChart
        );
    }

    @SuppressWarnings("unchecked")
    public void updateMetricsCharts(List<Process> scheduled) {
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

        // Safe way to add series without generic array warning
        metricsChart.getData().add(tatSeries);
        metricsChart.getData().add(wtSeries);
        metricsChart.getData().add(rtSeries);

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
}