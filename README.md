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
1. FCFS
    Input 4 processess
    P1 - Arrival: 6, Burst: 3
    P2 - Arrival: 1, Burst: 4
    P3 - Arrival: 9, Burst: 4
    P4 - Arrival: 8, Burst: 4

    Output:
    Process AT      BT      ST      CT      TAT     RT
    P2      1       4       1       5       4       0
    P1      6       3       6       9       3       0
    P4      8       4       9       13      5       1
    P3      9       4       13      17      8       4
    Average Turnaround time: 5.00
    Average Response Time: 1.25
2. SJF
    Input 3 processes
    P1 - Arrival: 5, Burst: 2
    P2 - Arrival: 2, Burst: 1
    P3 - Arrival: 7, Burst: 1
    Output:
    Process AT      BT      ST      CT      TAT     RT
    P2      2       1       2       3       1       0
    P1      5       2       5       7       2       0
    P3      7       1       7       8       1       0
    Average Turnaround time: 1.33
    Average Response Time: 0.00
3. SRTF
    Input 4 processes
    P1 - Arrival: 9, Burst: 5
    P2 - Arrival: 3, Burst: 2
    P3 - Arrival: 9, Burst: 5
    P4 - Arrival: 7, Burst: 3
    Output:
    Process AT      BT      ST      CT      TAT     RT
    P2      3       2       3       5       2       0
    P4      7       3       7       10      3       0
    P1      9       5       10      15      6       1
    P3      9       5       15      20      11      6
    Average Turnaround time: 5.50
    Average Response Time: 1.75
4. RR
    Input 4 processes
    P1 - Arrival: 9, Burst: 4
    P2 - Arrival: 1, Burst: 3
    P3 - Arrival: 1, Burst: 3
    P4 - Arrival: 2, Burst: 3
    Output:
    Process AT      BT      ST      CT      TAT     RT
    P1      9       4       10      14      5       1
    P2      1       3       1       4       3       0
    P3      1       3       4       7       6       3
    P4      2       3       7       10      8       5
    Average Turnaround time: 5.50
    Average Response Time: 2.25
5. MLFQ 
    Input 4 Processes
    P1 - Arrival: 0, Burst: 8
    P2 - Arrival: 1, Burst: 6
    P3 - Arrival: 3, Burst: 4
    P4 - Arrival: 5, Burst: 2
    Process AT      BT      ST      CT      TAT     RT
    P4      5       2       10      12      7       5
    P3      3       4       6       14      11      3
    P1      0       8       0       18      18      0
    P2      1       6       4       20      19      3
    Average Turnaround time: 13.75
    Average Response Time: 2.75
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

