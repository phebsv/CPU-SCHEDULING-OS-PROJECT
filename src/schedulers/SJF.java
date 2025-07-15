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


        printGanttChart(completedProcesses);
        printMetrics(completedProcesses);

        return completedProcesses;

    }

    private void printGanttChart(List<Process> processes){
        System.out.println("\nGantt Chart: ");
        System.out.print("|");
        for(Process p : processes){
            System.out.println(" " + p.getPid() + " |");

        }
        System.out.println();

        int time = 0;
        System.out.print("0");
        for (Process p : processes){
            time = p.getCompletionTime();
            System.out.println("   "+ time);
        }
        System.out.println("\n");
    }

    private void printMetrics(List<Process> processes){

        double totalTAT = 0;
        double totalRT = 0;

        System.out.println("PID\tBT\tCT\tTAT\tRT");
        for(Process p : processes){
            int at = p.getArrivalTime();
            int bt = p.getBurstTime();
            int ct = p.getCompletionTime();
            int tat = p.getTurnaroundTime();
            int rt = p.getResponseTime();

            totalTAT += tat;
            totalRT += rt;

            System.out.printf("%s\t%d\t%d\t%d\t%d\t%d\n", p.getPid(), at, bt, ct, tat, rt);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", totalTAT / processes.size());
        System.out.printf("Average Response Time: %.2f\n", totalRT / processes.size());
    }


}
