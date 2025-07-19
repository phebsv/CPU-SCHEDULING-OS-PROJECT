# CPU-SCHEDULING-OS-PROJECT

**PROJECT OVERVIEW**

This project aims to create a simulation tool that visually demonstrates how various CPU scheduling algorithms work. The simulation helps users understand the execution sequence of processes (displayed as a Gantt chart), their completion times, and computes key metrics like waiting time, turnaround time, and CPU utilization.

**HOW TO RUN THE SIMULATION**

**SCHEDULING ALGORITHMS IN THIS PROJECT**
1. _First Come First Serve (FCFS)_ =
2. _Shortest Job First (SJF - Non-Preemptive)_ =
3. _Shortest Remaining Time First (SRTF - Pre-emptive)_ =
4. _Round Robin (RR)_ =
5. _Multi-level Feedback Queue (MLFQ)_ =

**Screenshots**

**Sample Input and Output**

**Bugs**
1. Queue Demotion Timing Issue - Processes are not being demoted to lower priority queues at the correct time.
2. Queue Level Tracking Issue - The scheduler incorrectly tracks which queue processes belong to.

**Incomplete Features**
1. No boost time feature in MLFQ

**Members and its' contribution**

_Reiner Seldon C. Dela Cerna_ 
- Shortest Job First (SJF) - Non-preemptive
- Round Robin (RR)
- Designed responsive GUI with JavaFX

_Phoebe Reese Carmel D. Villaflor_ 
- First Come First Serve (FCFS)
- Shortest Remaining Time First (SRTF)
- Multi-Level Feedback Queue (MLFQ)
- Developed debug mode with step-through execution
- Created test harness for algorithm verification

