package main;

public class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int startTime = -1;

    public Process(String pid, int arrivalTime, int burstTime){
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public int getTurnaroundTime(){
        return completionTime - arrivalTime;
    }

    public int getResponseTime(){
        return startTime - arrivalTime;
    }
}