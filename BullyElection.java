import java.util.Scanner;

class Process {
    public int id;        // ID do processo
    public boolean status; // Status do processo ("active" ou "inactive")

    // Construtor para inicializar ID e status padrão como "active"
    public Process(int id) {
        this.id = id;
        this.status = true;
    }
}

public class BullyElection {

    private Scanner sc;          
    private Process[] processes; 
    private int n;               
    private int c, f;            

    public BullyElection() {
        sc = new Scanner(System.in);
    }

    public void runProgram() {
        while (true) {
            ring();
            performElection();

            System.out.println("Do you want to recover a process? (yes/no): ");
            String recoverInput = sc.next();

            if (recoverInput.equalsIgnoreCase("yes")) {
                recoverProcess();
            }

            System.out.println("Do you want to run another case? (yes/no): ");
            String userInput = sc.next();

            if (userInput.equalsIgnoreCase("no")) {
                System.out.println("Exiting the program.");
                break;
            }
        }
    }

    public void ring() {
        if (processes == null) { 
            System.out.print("Enter total number of processes: ");
            n = sc.nextInt();

            processes = new Process[n];
            for (int i = 0; i < n; i++) {
                processes[i] = new Process(i);
            }
        }

        System.out.print("What process will fault? ");
        f = sc.nextInt();
        processes[f].status = false;

        System.out.print("What process detects the fault? ");
        c = sc.nextInt();
    }

    public void performElection() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int initiator = c; 
        boolean electionOngoing = true;

        while (electionOngoing) {
            boolean higherProcessFound = false;

            for (int i = initiator + 1; i < n; i++) {
                if (processes[i].status) {
                    System.out.println("Process " + initiator + " sends Election(" + initiator + ") to Process " + i);
                    higherProcessFound = true;
                }
            }

            if (higherProcessFound) {
                for (int i = initiator + 1; i < n; i++) {
                    if (processes[i].status) {
                        System.out.println("Process " + i + " responds OK(" + i + ") to Process " + initiator);
                    }
                }
                initiator++;
            } else {
                int newCoordinator = processes[getMaxValue()].id;
                System.out.println("Finally, Process " + newCoordinator + " becomes the Coordinator.");

                for (int i = newCoordinator - 1; i >= 0; i--) {
                    if (processes[i].status) {
                        System.out.println("Process " + newCoordinator + " sends Coordinator(" + newCoordinator + ") to Process " + i);
                    }
                }

                System.out.println("End of Election.");
                electionOngoing = false;
            }
        }
    }

    private int getMaxValue() {
        int maxId = -1;
        int maxIndex = -1;

        for (int i = 0; i < processes.length; i++) {
            if (processes[i].status && processes[i].id > maxId) {
                maxId = processes[i].id;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public void recoverProcess() {
        System.out.print("Enter the process ID to recover: ");
        int processId = sc.nextInt();

        if (!processes[processId].status) {
            processes[processId].status = true;
            System.out.println("Process " + processId + " is recovered.");

            int maxProcess = getMaxValue();
            if (processId == maxProcess) {
                System.out.println("Process " + processId + " is the highest ID and assumes as Coordinator.");

                for (int i = 0; i < n; i++) {
                    if (i != processId && processes[i].status) {
                        System.out.println("Process " + processId + " sends Coordinator(" + processId + ") to Process " + i);
                    }
                }
            } else {
                for (int i = 0; i < n; i++) {
                    if (i != processId && processes[i].status) {
                        System.out.println("Process " + processId + " notifies Process " + i + " about recovery.");
                    }
                }
            }
        } else {
            System.out.println("Process " + processId + " is already active.");
        }
    }

    public static void main(String[] args) {
        BullyElection bully = new BullyElection();
        bully.runProgram();
    }
}