package schedulers;
import main.Process;
import main.Scheduler;

public class FCFS implements Scheduler{
    public List<Process> schedule(List<Process> processes){

        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<Process> scheduled = new ArrayList<>();
        int currentTime = 0;

        for(Process p : processes){
            if (currentTime < p.getArrivalTime()){
                currentTime = p.getArrivalTime();
            }

            p.setStartTime(currentTime);
            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);

            scheduled.add(p);
        }
        return scheduled;
    }
}