# CPU-SCHEDULING-OS-PROJECT

**PROJECT OVERVIEW**

This project aims to create a simulation tool that visually demonstrates how various CPU scheduling algorithms work. The simulation helps users understand the execution sequence of processes (displayed as a Gantt chart), their completion times, and computes key metrics like waiting time, turnaround time, and CPU utilization.

**HOW TO RUN THE SIMULATION**

1. You can manually input or generate random processes depending on how many processes you want to input.
2. Choose an algorithm scheduler.
   NOTE: If you chose RR or MLFQ algorithm, you must first input a value for Quantum for RR and Quantum time and Allotment time for MLFQ.
3. Run scheduler. It will direct you to the RESULT panel.
4. You can press Play to show the execution details.
5. You can also go to the Metrics panel for visualizing the Process Metrics and Process Execution Timeline.
6. If you want to save the output, you can press export and it will save you a text file.

**SCHEDULING ALGORITHMS IN THIS PROJECT**
1. _First Come First Serve (FCFS)_ = Executes processes in the order they arrive, without preemption; simple but may lead to long waiting times for short jobs.
2. _Shortest Job First (SJF - Non-Preemptive)_ = Runs the process with the shortest burst time first, minimizing average waiting time but requires prior knowledge of execution times.
3. _Shortest Remaining Time First (SRTF - Pre-emptive)_ = Preemptive version of SJF, where the scheduler always picks the job closest to completion, improving response times.
4. _Round Robin (RR)_ = Assigns fixed time slices (quantum) to each process in cyclic order, ensuring fair CPU allocation but may increase overhead with short quanta.
5. _Multi-level Feedback Queue (MLFQ)_ =  Uses multiple priority queues with dynamic process promotion/demotion, balancing responsiveness and throughput for varied process types.

**Screenshots**
<img width="1495" height="871" alt="image" src="https://github.com/user-attachments/assets/b5a6d5db-d9c1-4c37-9f6d-110bb4c2b1fd" />
<img width="1497" height="847" alt="image" src="https://github.com/user-attachments/assets/76873f2a-fbcc-4938-9f9c-a3a3b550c596" />
<img width="1496" height="837" alt="image" src="https://github.com/user-attachments/assets/ccfefdd8-05c1-4f02-803f-22846d7c5844" />
<img width="1495" height="858" alt="image" src="https://github.com/user-attachments/assets/8e6f62f3-861a-4be9-9172-761796335752" />
<img width="1488" height="877" alt="image" src="https://github.com/user-attachments/assets/d1db2a26-97e3-4b3d-87e2-09d75b05794e" />


**Sample Input and Output**
1. FCFS
    Input 4 processess
    P1 - Arrival: 6, Burst: 3
    P2 - Arrival: 1, Burst: 4
    P3 - Arrival: 9, Burst: 4
    P4 - Arrival: 8, Burst: 4

![6B8906E3-7323-4AF0-934B-5A643A64A850_4_5005_c](https://github.com/user-attachments/assets/db67da93-13b9-476a-9daf-dac335d5cbf3)

2. SJF
    Input 3 processes
    P1 - Arrival: 5, Burst: 2
    P2 - Arrival: 2, Burst: 1
    P3 - Arrival: 7, Burst: 1
![E73F2D39-919F-4B27-96A6-0DD63EFAB83C_4_5005_c](https://github.com/user-attachments/assets/98a71265-2275-474c-a2c0-df8cc3f008f5)

      
3. SRTF
    Input 4 processes
    P1 - Arrival: 9, Burst: 5
    P2 - Arrival: 3, Burst: 2
    P3 - Arrival: 9, Burst: 5
    P4 - Arrival: 7, Burst: 3
   
![06D2564D-9F8F-4593-91E6-6280C04BA15E_4_5005_c](https://github.com/user-attachments/assets/6ff2e01e-da21-4fbe-9dcd-4bcad9ccbbea)

   
4. RR
    Input 4 processes
    P1 - Arrival: 9, Burst: 4
    P2 - Arrival: 1, Burst: 3
    P3 - Arrival: 1, Burst: 3
    P4 - Arrival: 2, Burst: 3
   
![542A9E7B-4D5A-4230-97A0-D55C6B912933_4_5005_c](https://github.com/user-attachments/assets/880c2cc9-17d5-4744-bacf-326899868f01)
   
5. MLFQ
    Input 4 Processes
    P1 - Arrival: 0, Burst: 8
    P2 - Arrival: 1, Burst: 6
    P3 - Arrival: 3, Burst: 4
    P4 - Arrival: 5, Burst: 2
    Q0 time quantum: 2
    Q0 allotment time: 4
    Q1 time quantum: 4
    Q1 allotment time: 8
    Q2 time quantum: 6
    Q2 allotment time: 4
    Q3 time quantum: 8
    Q3 allotment time: 2

![EDDA2017-F483-45D5-9658-564017539AAD_4_5005_c](https://github.com/user-attachments/assets/067bf8e1-a72d-456f-920b-2f945acaf211)


**Bugs**
1. MLFQ Scheduling
   - Queue Demotion Timing Issue - Processes are not being demoted to lower priority queues at the correct time.
   - Queue Level Tracking Issue - The scheduler incorrectly tracks which queue processes belong to.
2. Gantt Chart
   - SRTF inaccurate gantt chart in GUI. However, terminal output shows the accurate gantt chart for the program.
   - Some visualization of the Gantt Chart isn't aligned with the time.

**Limitations**
1. MLFQ implementation uses dynamic queue-based priorities only. Static process priorities are not supported

**Incomplete Features**
1. No boost time feature in MLFQ
2. No context switch delay

**Members and its' contribution**

_Reiner Seldon C. Dela Cerna_ 
- Shortest Job First (SJF) - Non-preemptive
- Round Robin (RR)
- Designed responsive GUI with JavaFX
- Debug GUI

_Phoebe Reese Carmel D. Villaflor_ 
- First Come First Serve (FCFS)
- Shortest Remaining Time First (SRTF)
- Multi-Level Feedback Queue (MLFQ)
- Debug Algorithms
- Does run test for algorithm verification

