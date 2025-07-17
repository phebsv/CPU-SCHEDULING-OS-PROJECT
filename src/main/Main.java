package main;
import java.util.*;
import schedulers.FCFS;
import schedulers.MLFQ;
import schedulers.RoundRobin;
import schedulers.SJF;
import schedulers.SRTF;


public class Main{
    public static void main(String[] args){
        try (Scanner scanner = new Scanner (System.in)) {
            System.out.println("CPU Scheduling Visualization");
            System.out.println("------------------------------------------");
            System.out.println("1. Input manually");
            System.out.println("2. Generate randomly");
            System.out.print("Select input method: ");
            int inputChoice = scanner.nextInt();

            List<Process> processes = new ArrayList<>();

            if (inputChoice == 1){
                System.out.print("Enter number of processes: ");
                int n = scanner.nextInt();
                for (int i=0; i<n; i++){
                    System.out.println("Process P" + (i+1));
                    System.out.print("Arrival Time: ");
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
            System.out.println("4. Round Robin(RR)");
            System.out.println("5. Multi-level Feedback Queue (MLFQ)");
            System.out.print("Your Choice: ");
            int algoChoice = scanner.nextInt();

            Scheduler scheduler = switch(algoChoice){
                case 1 -> new FCFS();
                case 2 -> new SJF();
                case 3 -> new SRTF();
                case 4 -> {
                    System.out.println("Enter time quantum: ");
                    int quantum = scanner.nextInt();
                    yield new RoundRobin(quantum);
                }
                case 5 -> {
                    int levels = 4;
                    int [] timeQuanta = new int[levels];
                    int [] allotmentTimes = new int[levels];
                    System.out.println("Enter time quantum and allotment time for each level: ");
                    for (int i =0; i < levels; i++) {
                        System.out.printf("Q%d time quantum: ", i);
                        timeQuanta[i] = scanner.nextInt();
                        System.out.printf("Q%d allotment time: ", i);
                        allotmentTimes[i] = scanner.nextInt();
                    }
                    yield new schedulers.MLFQ(timeQuanta, allotmentTimes);
                }
                default -> throw new IllegalArgumentException("Invalid Choice");
            };
            

            List<Process> scheduled = scheduler.schedule(processes);
            
            System.out.println("Gantt Chart");
            if (algoChoice == 5 && scheduler instanceof MLFQ mlfqScheduler) {
                List<MLFQ.GanttEntry> entries = mlfqScheduler.getGanttEntries();

                if (entries.isEmpty()){
                    System.out.println("No Gantt Entries to display.");
                } else{
                    System.out.println("|");
                    for (MLFQ.GanttEntry entry : entries) {
                        if(entry.queueLevel == -1){
                            System.out.print("| IDLE");
                        } else {
                            System.out.print("| " + entry.pid + "(Q" + entry.queueLevel + ") ");
                        }
                    }
                    System.out.println();

                for (MLFQ.GanttEntry entry : entries) {
                    System.out.print(entry.startTime + "\t");
                }
                System.out.println(entries.get(entries.size() - 1).endTime);
            }
        } else if (algoChoice == 4 && scheduler instanceof RoundRobin rrScheduler) {
                List<Process> execOrder = rrScheduler.getExecutionOrder();

                if (execOrder.isEmpty()) {
                    System.out.println("No execution to display.");
                } else {
                    int time = 0;
                    for (Process p : execOrder) {
                        System.out.print("| " + p.getPid() + " ");
                    }
                    System.out.println("|");

                    System.out.print(execOrder.get(0).getStartTime());
                    for (Process p : execOrder) {
                        System.out.print("   " + p.getCompletionTime());
                    }
                    System.out.println();
            }
        } else {
                int currentTime = 0;
                for (Process p : scheduled) {
                    System.out.print("| " + p.getPid() + " ");
                }
                System.out.println("|");

                for (Process p : scheduled) {
                    System.out.print(p.getStartTime()+ "\t");
                }
                System.out.println(scheduled.get(scheduled.size() - 1).getCompletionTime());
            }

        System.out.println("Process\tAT\tBT\tST\tCT\tTAT\tRT");
        double totalTAT=0;
        double totalRT=0;

        for (Process p : scheduled){
            int tat= p.getCompletionTime() - p.getArrivalTime();
            int rt= p.getStartTime() - p.getArrivalTime();
            totalTAT += tat;
            totalRT += rt;

            System.out.println(p.getPid() + "\t" +p.getArrivalTime() + "\t" + p.getBurstTime() +"\t"
            + p.getStartTime() + "\t" + p.getCompletionTime() + "\t" + tat + "\t" +rt);
        }

        System.out.printf("Average Turnaround time: %.2f", totalTAT / scheduled.size());
        System.out.printf("Average Response Time: %.2f", totalRT / scheduled.size());
        }
    }
}
