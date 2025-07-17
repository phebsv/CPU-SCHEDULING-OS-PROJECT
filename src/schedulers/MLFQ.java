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
        int completed = 0;
        int n = allProcesses.size();
        Set<String> inQueue = new HashSet<>();

        Map<String, Integer> timeUsedAtLevel = new HashMap<>();

        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completed < n) {
            for (Process p : allProcesses) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                    queues[0].offer(p);
                    inQueue.add(p.getPid());
                    timeUsedAtLevel.put(p.getPid(),0);
                }
            }

            boolean didRun = false;

            for (int level = 0; level < queues.length; level++) {
                Queue<Process> queue = queues[level];

                if (!queue.isEmpty()) {
                    Process current = queue.poll();
                    inQueue.remove(current.getPid());
                    current.setQueueLevel(level);

                    if (current.getStartTime() == -1) {
                        current.setStartTime(currentTime);
                    }

                    int quantum = timeQuantums[level];
                    int runTime = Math.min(quantum, current.getRemainingTime());

                    int startRunTime = currentTime;

                    for (int t = 0; t < runTime; t++) {
                        currentTime++;
                        current.setRemainingTime(current.getRemainingTime()-1);

                        for (Process p : allProcesses) {
                            if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                                queues[0].offer(p);
                                inQueue.add(p.getPid());
                                timeUsedAtLevel.put(p.getPid(),0);
                            }
                        }

                        if (current.getRemainingTime() == 0) {
                            current.setCompletionTime(currentTime);
                            completedProcesses.add(current);
                            completed++;
                            break;
                        }
                    }

                    if (startRunTime < currentTime){
                    ganttEntries.add(new GanttEntry(current.getPid(), level, startRunTime, currentTime));
                    }
                    
                    int used = timeUsedAtLevel.getOrDefault(current.getPid(), 0);
                    timeUsedAtLevel.put(current.getPid(), used + runTime);

                    if (current.getRemainingTime() > 0) {
                        if (timeUsedAtLevel.get(current.getPid()) >= allotmentTimes[level] && level < queues.length -1){
                            queues[level + 1].offer(current);
                            inQueue.add(current.getPid());
                            timeUsedAtLevel.put(current.getPid(),0);
                        } else{
                        queues[level].offer(current);
                        inQueue.add(current.getPid());
                        }
                    }

                    didRun = true;
                    break;
                }
            }

            if (!didRun) {
                int idleStart = currentTime;
                currentTime++;
                ganttEntries.add(new GanttEntry("IDLE", -1, idleStart, currentTime));
            }
        }

        //ganttEntries.clear();

        return completedProcesses;
    }
}
