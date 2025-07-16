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

    public String getPid(){ 
        return pid;
    }
    public int getArrivalTime(){
        return arrivalTime;
    }
    public int getBurstTime(){
        return burstTime;
    }
    public int RemainingTime(){
        return remainingTime;
    }
    public int getCompletionTime(){
        return completionTime;
    }
    public int getStartTime(){
        return startTime;
    }
    public int getRemainingTime(){
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime){
        this.remainingTime = remainingTime;
    }
    public void setCompletionTime(int completionTime){
        this.completionTime = completionTime;
    }
    public void setStartTime(int startTime){
        this.startTime = startTime;
    }
    
    public int getTurnaroundTime(){
        return completionTime - arrivalTime;
    }

    public int getResponseTime(){
        return startTime - arrivalTime;
    }
}