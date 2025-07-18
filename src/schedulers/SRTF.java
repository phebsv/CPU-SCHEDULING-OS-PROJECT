package schedulers;
import java.util.*;
import main.Process;
import main.Scheduler;


public class SRTF implements Scheduler {

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

    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processes){
            allProcesses.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime()));
        }
        List<Process> readyQueue = new ArrayList<>();
        List<Process> completed = new ArrayList<>();
        ganttChart.clear();

        int currentTime = 0;
        int completedCount = 0;
        int n = processes.size();
        Process currentProcess = null;
        int currentProcessStartTime = -1;

        while (completedCount < n) {
            Iterator<Process> it = allProcesses.iterator(); 
            while (it.hasNext()){
                Process p = it.next();
                if (p.getArrivalTime() <= currentTime){
                    readyQueue.add(p);
                    it.remove();
                }
            }

            readyQueue.removeIf(p -> p.getRemainingTime() <= 0);

            Process shortestProcess = null;
            if (!readyQueue.isEmpty()) {
                shortestProcess = readyQueue.stream()
                    .min(Comparator.comparingInt(Process::getRemainingTime))
                    .orElse(null);
            }

            boolean needsPreemption = false;
            if (currentProcess == null) {
                needsPreemption = true;
            } else if (shortestProcess != null && shortestProcess.getRemainingTime() < currentProcess.getRemainingTime() && currentProcess.getRemainingTime() > 0) {
                needsPreemption = true;
            }

            if (needsPreemption) {
                if (currentProcess != null && currentProcessStartTime != -1) {
                    ganttChart.add(new GanttEntry(currentProcess.getPid(), currentProcessStartTime, currentTime));
                }
            
                currentProcess = shortestProcess;
                currentProcessStartTime = currentTime;
            }

            if (currentProcess != null && currentProcess.getRemainingTime() > 0) {
                if (currentProcess.getStartTime() == -1) {
                    currentProcess.setStartTime(currentTime);
                }
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                currentTime++;

                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.setCompletionTime(currentTime);
                    completed.add(currentProcess);
                    completedCount++;
                    
                    ganttChart.add(new GanttEntry(currentProcess.getPid(), currentProcessStartTime, currentTime));

                    currentProcess = null;
                    currentProcessStartTime = -1;
                }
            } else {
                if (currentProcess != null && currentProcessStartTime != -1) {
                    ganttChart.add(new GanttEntry(currentProcess.getPid(), currentProcessStartTime, currentTime));
                }
                
                ganttChart.add(new GanttEntry("IDLE", currentTime, currentTime + 1));
                currentTime++;
                currentProcess = null;
            currentProcessStartTime = -1;
            }
        }
        completed.sort(Comparator.comparingInt(Process::getStartTime));
        return completed;
    }
        public List<GanttEntry> getGanttEntries() {
            return ganttChart;
    }
}
