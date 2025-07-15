package main;
import java.util.*;

import schedulers.FCFS;
import schedulers.SJF;

public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner (System.in);

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
        System.out.print("Your Choice: ");
        int algoChoice = scanner.nextInt();

        Scheduler scheduler = null;

        switch(algoChoice){
            case 1 :
            scheduler = new FCFS();
            break;
            case 2 :
            scheduler = new SJF();
            break;
            default: 
                System.out.println("Invalid Choice");
                System.exit(0);
        }
        
        scanner.close();
    }
}