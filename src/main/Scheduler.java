package main;
import java.util.List;

public interface Scheduler{
    List<Process> schedule(List<Process> processes);
}