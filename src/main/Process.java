package main;

public class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int completionTime;
    int turnaroundTime;
    int responseTime;
    int startTime = -1;
    int queueLevel = -1;

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
    public int getRemainingTime(){
        return remainingTime;
    }
    public int getCompletionTime(){
        return completionTime;
    }
    public int getStartTime(){
        return startTime;
    }
    public int getQueueLevel(){
        return queueLevel;
    }

    public int getTurnaroundTime(){
        return turnaroundTime;
    }

    public int getResponseTime(){
        return responseTime;
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
    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }
    public void setQueueLevel(int level){
        this.queueLevel = level;
    }
    public void setTurnaroundTime(int turnaroundTime){
        this.turnaroundTime = turnaroundTime;
    }
    public void setResponseTime(int responseTime){
        this.responseTime = responseTime;
    }
}