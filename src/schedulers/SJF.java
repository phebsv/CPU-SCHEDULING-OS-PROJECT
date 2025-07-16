package schedulers;
import main.Process;
import main.Scheduler;

import java.util.*;


public class SJF implements Scheduler { 
    @Override
    public List<Process> schedule(List<Process> processes){
        List<Process> completedProcesses = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>();
        List<Process> allProcesses = new ArrayList<>(processes);

        int currentTime = 0;

        while (!allProcesses.isEmpty() || !readyQueue.isEmpty()) {
            for (Iterator<Process> it = allProcesses.iterator(); it.hasNext();){
                Process p = it.next();
                if (p.getArrivalTime() <= currentTime){
                    readyQueue.add(p);
                    it.remove();
                }
            }
        
            if (readyQueue.isEmpty()){
                currentTime++;
                continue;
            }

            readyQueue.sort(Comparator.comparingInt(Process::getBurstTime));

            Process current = readyQueue.remove(0);
            if(current.getStartTime() == -1){
                current.setStartTime(currentTime);
            }

            currentTime += current.getBurstTime();
            current.setCompletionTime(currentTime);
            completedProcesses.add(current);



            
        }


        return completedProcesses;

    }

    

}
