package schedulers;

import java.util.*;
import main.Process;
import main.Scheduler;

public class RoundRobin implements Scheduler {

    private final int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> allProcesses = new ArrayList<>(processes);
        List<Process> completedProcesses = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        List<Process> executionOrder = new ArrayList<>();

        int currentTime= 0;
        int completed = 0;
        int n = allProcesses.size();
    public List<Process> schedule(List<Process> processes) {
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processes) {
            Process copy = new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime());
            allProcesses.add(copy);
        }

        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue<Process> readyQueue = new LinkedList<>();
        int currentTime = 0, completed = 0;
        Set<String> inQueue = new HashSet<>();
        List<Process> completedProcesses = new ArrayList<>();

        Set<String> inQueue = new HashSet<>();


        while (completed<n) {
        while (completed < allProcesses.size()) {
            for (Process p : allProcesses) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                    readyQueue.offer(p);
                    inQueue.add(p.getPid());
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            inQueue.remove(current.getPid());

            if (current.getStartTime() == -1) {
                current.setStartTime(currentTime);
            }

            Process exec = new Process(current.getPid(), current.getArrivalTime(), current.getBurstTime());
            exec.setStartTime(currentTime);
            executionOrder.add(exec);
            

            int executeTime = Math.min(quantum, current.getRemainingTime());
            currentTime += executeTime;
            current.setRemainingTime(current.getRemainingTime() - executeTime);

            if (current.getRemainingTime() == 0){

            int timeSlice = Math.min(quantum, current.getRemainingTime());
            int start = currentTime;
            int end = currentTime + timeSlice;

            Process execSegment = new Process(current.getPid(), current.getArrivalTime(), current.getBurstTime());
            execSegment.setStartTime(start);
            execSegment.setCompletionTime(end);
            executionOrder.add(execSegment);

            currentTime += timeSlice;
            current.setRemainingTime(current.getRemainingTime() - timeSlice);

            for (Process p : allProcesses) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())) {
                    readyQueue.offer(p);
                    inQueue.add(p.getPid());
                }
            }

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(currentTime);
                current.setTurnaroundTime(current.getCompletionTime() - current.getArrivalTime());
                current.setResponseTime(current.getStartTime() - current.getArrivalTime());
                completedProcesses.add(current);
                completed++;
            }else {
                
                for (Process p : allProcesses){
                    if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())){
                        queue.offer(p);
                        inQueue.add(p.getPid());
                    }
                }

                queue.offer(current);

            } else {
                readyQueue.offer(current);
                inQueue.add(current.getPid());
            }
        }

        return completedProcesses;


    }

    public List<Process> getExecutionOrder() {
        return executionOrder;
    }
}
