package main;
import java.util.*;
import schedulers.FCFS;
import schedulers.SJF;
import schedulers.SRTF;

public class Main{
    public static void main(String[] args){
        try (Scanner scanner = new Scanner (System.in)) {
            System.out.println("CPU Scheduling Visualization");
            System.out.println("------------------------------------------");
            System.out.println("1. Input manually");
            System.out.println("2. Generate randomly");
            System.out.println("Select input method: ");
            int inputChoice = scanner.nextInt();

            List<Process> processes = new ArrayList<>();

            if (inputChoice == 1){
                System.out.print("Enter number of processes: ");
                int n = scanner.nextInt();
                for (int i=0; i<n; i++){
                    System.out.println("Process P" + (i+1));
                    System.out.println("Arrival Time: ");
                    int at = scanner.nextInt();
                    System.out.print("Burst Time: ");
                    int bt = scanner.nextInt();
                    processes.add(new Process("P"+ (i+1), at, bt));
                }
            }else {
                System.out.print("Enter number of processes: ");
                Random rand = new Random();
                int n = scanner.nextInt();
                for (int i=0; i<n; i++){
                    int at = rand.nextInt(10);
                    int bt = rand.nextInt(5) + 1;
                    processes.add(new Process("P"+(i+1), at, bt));
                }
                System.out.println("Generated processes: ");
                for (Process p : processes){
                    System.out.println(p.getPid() + " - Arrival: " + p.getArrivalTime()+ ", Burst: " + p.getBurstTime());
                }
            }
            System.out.println("Choose a Scheduling Algorithm");
            System.out.println("1. First Come First Serve(FCFS)");
            System.out.println("2. Shortest Job First (SJF - Non-Preemptive)");
            System.out.println("3. Shortest Remaining Time First (SRTF)");
            System.out.print("Your Choice: ");
            int algoChoice = scanner.nextInt();

            Scheduler scheduler = switch(algoChoice){
                case 1 -> new FCFS();
                case 2 -> new SJF();
                case 3 -> new SRTF();
                default -> throw new IllegalArgumentException("Invalid Choice");
            };
            

            List<Process> scheduled = scheduler.schedule(processes);
            
            System.out.println("Gantt Chart");
            for (Process p : scheduled){
                System.out.print("| " + p.getPid()+" ");
            }
            System.out.println("|");

            int time = scheduled.get(0).getStartTime();
            System.out.print(time);
            for(Process p:scheduled){
                time = p.getCompletionTime();
                System.out.print("   " + time);
            }

        System.out.println();

        double totalTAT=0;
        double totalRT=0;

        System.out.println("Process\tAT\tBT\tST\tCT\tTAT\tRT");
        for (Process p : scheduled){
            int tat= p.getTurnaroundTime();
            int rt= p.getResponseTime();
            totalTAT += tat;
            totalRT += rt;

            System.out.println(p.getPid() + "\t" +p.getArrivalTime() + "\t" + p.getBurstTime() +"\t"
            + p.getStartTime() + "\t" + p.getCompletionTime() + "\t" + tat + "\t" +rt);
        }

        int n = scheduled.size();
        System.out.printf("Average Turnaround time: %.2f", totalTAT / n);
        System.out.printf("Average Response Time: %.2f", totalRT / n);
    }
}
}