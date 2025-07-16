package schedulers;

import main.Process;
import main.Scheduler;
import java.util.*;

public class SRTF implements Scheduler { 
    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> allProcesses = new ArrayList<>(processes);
        List<Process> readyQueue = new ArrayList<>();
        List<Process> completed = new ArrayList<>();

        int currentTime = 0;
        int completedCount = 0;
        int n = allProcesses.size();

        while (completedCount < n) {
            Iterator<Process> it = allProcesses.iterator(); 
            while (it.hasNext()){
                Process p = it.next();
                if (p.getArrivalTime() <= currentTime){
                    readyQueue.add(p);
                    it.remove();
                }
            }

            Process current = readyQueue.stream()
                    .filter(p -> p.getRemainingTime() > 0)
                    .min(Comparator.comparingInt(Process::getRemainingTime))
                    .orElse(null);
        
            if (current != null){
                if (current.getStartTime() == -1){
                    current.setStartTime(currentTime);
                }
                
                current.setRemainingTime(current.getRemainingTime()-1);
                currentTime++;

                if (current.getRemainingTime() == 0){
                    current.setCompletionTime(currentTime);
                    completed.add(current);
                    readyQueue.remove(current);
                    completedCount++;
                }
            } else{
                currentTime++;
            }
    }
    completed.sort(Comparator.comparingInt(Process::getStartTime));
    return completed;
}
}
