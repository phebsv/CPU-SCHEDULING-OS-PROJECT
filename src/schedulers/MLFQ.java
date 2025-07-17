package schedulers;
import main.Process;
import main.Scheduler;

import java.util.*;

public class MLFQ implements Scheduler {

    private final int[] timeQuantums;

    public MLFQ(int[] timeQuantums) {
        this.timeQuantums = timeQuantums;
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

        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completed < n) {
            for (Process p : allProcesses) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                    queues[0].offer(p);
                    inQueue.add(p.getPid());
                }
            }

            boolean didRun = false;

            for (int level = 0; level < queues.length; level++) {
                Queue<Process> queue = queues[level];

                if (!queue.isEmpty()) {
                    Process current = queue.poll();
                    inQueue.remove(current.getPid());

                    if (current.getStartTime() == -1) {
                        current.setStartTime(currentTime);
                    }

                    int quantum = timeQuantums[level];
                    int runTime = Math.min(quantum, current.getRemainingTime());

                    for (int t = 0; t < runTime; t++) {
                        currentTime++;

                        for (Process p : allProcesses) {
                            if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                                queues[0].offer(p);
                                inQueue.add(p.getPid());
                            }
                        }

                        current.setRemainingTime(current.getRemainingTime() - 1);
                        if (current.getRemainingTime() == 0) {
                            current.setCompletionTime(currentTime);
                            completedProcesses.add(current);
                            completed++;
                            didRun = true;
                            break;
                        }
                    }

                    if (current.getRemainingTime() > 0) {
                        int nextLevel = Math.min(level + 1, queues.length - 1);
                        queues[nextLevel].offer(current);
                        inQueue.add(current.getPid());
                    }

                    didRun = true;
                    break;
                }
            }

            if (!didRun) {
                currentTime++;
            }
        }

        return completedProcesses;
    }
}
