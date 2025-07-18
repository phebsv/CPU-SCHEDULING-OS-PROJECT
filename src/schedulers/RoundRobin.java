package schedulers;

import java.util.*;
import main.Process;
import main.Scheduler;

public class RoundRobin implements Scheduler {

    private final int quantum;
    private List<GanttEntry> ganttChart = new ArrayList<>();

    public static class GanttEntry {
        public String pid;
        public int startTime;
        public int endTime;
        
        public GanttEntry(String pid, int startTime, int endTime) {
            this.pid = pid;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processes) {
            allProcesses.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime()));
        }

        List<Process> completedProcesses = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        ganttChart.clear();

        int currentTime= 0;
        int completed = 0;
        int n = allProcesses.size();
        int processIndex=0;

        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completed < n) {
            while (processIndex < allProcesses.size() && 
                allProcesses.get(processIndex).getArrivalTime() <= currentTime) {
                readyQueue.offer(allProcesses.get(processIndex));
                processIndex++;
            }

            if (readyQueue.isEmpty()) {
                if (processIndex < allProcesses.size()) {
                    int nextArrival = allProcesses.get(processIndex).getArrivalTime();
                    ganttChart.add(new GanttEntry("IDLE", currentTime, nextArrival));
                    currentTime = nextArrival;
                } else {
                    currentTime++;
                }
                continue;
            }


            Process current = readyQueue.poll();
            if (current.getStartTime() == -1) {
                current.setStartTime(currentTime);
            }

            int executeTime = Math.min(quantum, current.getRemainingTime());
            int segmentStartTime = currentTime;
            current.setRemainingTime(current.getRemainingTime() - executeTime);
            currentTime += executeTime;

            ganttChart.add(new GanttEntry(current.getPid(), segmentStartTime, currentTime));

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(currentTime);
                completedProcesses.add(current);
                completed++;
            } else {
                while (processIndex < allProcesses.size() && 
                    allProcesses.get(processIndex).getArrivalTime() <= currentTime) {
                    readyQueue.offer(allProcesses.get(processIndex));
                    processIndex++;
                }

                readyQueue.offer(current);
            }
        }
        completedProcesses.sort(Comparator.comparing(Process::getPid));
        return completedProcesses;
    }
    public List<GanttEntry> getGanttEntries() {
        return ganttChart;
    }
}
