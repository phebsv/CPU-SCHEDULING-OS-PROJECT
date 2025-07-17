package schedulers;
import java.util.*;
import main.Process;
import main.Scheduler;


public class RoundRobin implements Scheduler {

    private final int quantum;
    private final List<Process> executionOrder = new ArrayList<>();

    public RoundRobin(int quantum){
        this.quantum = quantum;
    }

    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> allProcesses = new ArrayList<>(processes);
        List<Process> completedProcesses = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();
        Set<String> inQueue = new HashSet<>();

        int currentTime= 0;
        int completed = 0;
        int n = allProcesses.size();

        allProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completed<n) {

            for (Process p : allProcesses) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())){
                    queue.offer(p);
                    inQueue.add(p.getPid());
                }
            }

            if (queue.isEmpty()){
                currentTime++;
                continue;
            }
            
            Process current = queue.poll();
            inQueue.remove(current.getPid());

            if (current.getStartTime() == -1) {
                current.setStartTime(currentTime);
            }

            int executeTime = Math.min(quantum, current.getRemainingTime());

            Process exec = new Process(current.getPid(), current.getArrivalTime(), current.getBurstTime());
            exec.setStartTime(currentTime);
            exec.setCompletionTime(currentTime + executeTime);
            executionOrder.add(exec);
            
            currentTime += executeTime;
            current.setRemainingTime(current.getRemainingTime() - executeTime);

            for (Process p : allProcesses){
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0 && !inQueue.contains(p.getPid())){
                    queue.offer(p);
                    inQueue.add(p.getPid());
                    }
                }

            if (current.getRemainingTime() == 0){
                current.setCompletionTime(currentTime);
                completedProcesses.add(current);
                completed++;
            }else {
                queue.offer(current);
                inQueue.add(current.getPid());
            }
        }

        return completedProcesses;
    }
    public List<Process> getExecutionOrder(){
        return executionOrder;
    }
}



