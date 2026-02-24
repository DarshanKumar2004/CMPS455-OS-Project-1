// Used in task 1

import java.util.concurrent.Semaphore;

public class Barrier {
    private int count = 0;
    private int total;
    private String status;

    private Semaphore mutex = new Semaphore(1);
    private Semaphore unlock = new Semaphore(0);
    public Barrier (int total, String status) {
        this.total = total;
        this.status = status;
    }

    public void await() throws InterruptedException {
        mutex.acquire();
        count++;

        // Only release threads when all have been arrived to same point
        if (count == total) {
            unlock.release(total);
            System.out.println(status);
        }

        mutex.release();
        unlock.acquire();
    }
}
