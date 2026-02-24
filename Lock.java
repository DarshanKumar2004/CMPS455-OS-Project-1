// Used in task 1
import java.util.concurrent.Semaphore;

public class Lock {
    private final Semaphore mutex = new Semaphore(1);

    public void lock() throws InterruptedException {
        mutex.acquire();
    }

    public void unlock() {
        mutex.release();
    }
}
