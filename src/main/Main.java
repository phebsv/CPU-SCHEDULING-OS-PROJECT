package main;
import java.util.*;

public class Main{
    public stativ void main(String[] args){
        Scanner scanner = new Scanne (System.in);

        System.out.println("CPU Scheduling Visualization");
        System.out.println("------------------------------------------");
        System.out.println("1. Input manually");
        System.out.println("2. Generate randomly");
        System.out.println("Select input method: ");
        int inputChoice = scanner.nextInt();

        List<Process> processes = new ArrayList<>();

        if (intputChoice == 1){
            System.out.print("Enter number of processes: ");
            int n = scanner.nextInt();
            for (int i=0; i<n; i++){
                System.out.println("Process P" + (i+1));
                System.out.println("Arrival Time: ");
                int at = scanner.nextInt();
                System.out.print("Burst Time: ")
                int bt = scanner.nextInt();
                processes.add(new Process("P"+ (i+1), at, bt));
            }
        }else {
            System.out.print("Enter number of processes: ");
            Random rand = new Random();
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
    }
}