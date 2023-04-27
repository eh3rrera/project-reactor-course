package net.eherrera.reactor.m8.exercises;

public class Exercise03 {
    public static void main(String[] args) {
        // TODO: Create a Mono from the blockingOperation method

        // TODO: Run the blocking code on a bounded elastic scheduler

        // TODO: Subscribe to the Mono and print the emitted value
    }

    public static String blockingOperation() {
        try {
            // Simulate a blocking operation using Thread.sleep()
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Blocking operation completed";
    }
}
