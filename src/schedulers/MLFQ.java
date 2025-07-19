package schedulers;
import java.util.*;
import main.Process;
import main.Scheduler;

public class MLFQ implements Scheduler {
    private final int[] timeQuantums;
    private final int[] allotmentTimes;
    private final List<GanttEntry> ganttEntries = new ArrayList<>();

    public MLFQ(int[] timeQuantums, int[] allotmentTimes) {
        this.timeQuantums = timeQuantums;
        this.allotmentTimes = allotmentTimes;
    }

    public static class GanttEntry {
        public String pid;
        public int queueLevel;
        public int startTime;
        public int endTime;

        public GanttEntry(String pid, int queueLevel, int startTime, int endTime) {
            this.pid = pid;
            this.queueLevel = queueLevel;
            this.startTime = startTime;
            this.endTime = endTime;
    }
}

    public List<GanttEntry> getGanttEntries(){
        return ganttEntries;
    }

    @Override
    public List<Process> schedule(List<Process> processes) {
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processes) {
            allProcesses.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime()));
        }

        List<Process> completedProcesses = new ArrayList<>();
        Queue<Process>[] queues = new LinkedList[timeQuantums.length];
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new LinkedList<>();
        }

        int currentTime = 0;
        int totalProcesses = processes.size();
        Map<String, Integer> timeUsedPerQueue = new HashMap<>();
        Map<String, Integer> currentQueueLevel = new HashMap<>();

        for (Process p : processes) {
            currentQueueLevel.put(p.getPid(), 0);
            timeUsedPerQueue.put(p.getPid(), 0);
        }


        while (completedProcesses.size() < totalProcesses) {
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime &&
                    p.getRemainingTime() > 0 &&
                    !queues[currentQueueLevel.get(p.getPid())].contains(p)) {
            
                    if (!isInAnyQueue(queues, p)) {
                        currentQueueLevel.put(p.getPid(), 0);
                        timeUsedPerQueue.put(p.getPid(), 0);
                    }
            
                    queues[0].add(p);
                }
            }


            boolean executed = false;

            for (int level = 0; level < queues.length; level++) {
                if (!queues[level].isEmpty()) {
                    Process current = queues[level].remove();
                    executed = true;
                    
                    if (current.getStartTime() == -1) {
                        current.setStartTime(currentTime);
                    }


                    int quantum = timeQuantums[level];
                    int remainingTime = current.getRemainingTime();
                    int timeToRun = Math.min(quantum, remainingTime);

                    ganttEntries.add(new GanttEntry(current.getPid(), level, currentTime, currentTime + timeToRun));
                    current.setRemainingTime(remainingTime - timeToRun);

                    int timeUsed = timeUsedPerQueue.get(current.getPid()) + timeToRun;
                    timeUsedPerQueue.put(current.getPid(), timeUsed);
                    
                    currentTime += timeToRun;

                    if (current.getRemainingTime() == 0) {
                        current.setCompletionTime(currentTime);
                        completedProcesses.add(current);
                    } else if (timeUsed >= allotmentTimes[level] && level < queues.length - 1) {
                        currentQueueLevel.put(current.getPid(), level + 1);
                        timeUsedPerQueue.put(current.getPid(), 0);
                        queues[level + 1].add(current);
                    } else {
                        queues[level].add(current);
                    }
                    break;
                }
            }
        

            if (!executed) {
                currentTime++;
                ganttEntries.add(new GanttEntry("IDLE", -1, currentTime - 1, currentTime));
            }
    }
        return completedProcesses;
    }
    private boolean isInAnyQueue(Queue<Process>[] queues, Process p) {
        for (Queue<Process> queue : queues) {
            if (queue.contains(p)) {
                return true;
            }
        }
        return false;
    }
}
