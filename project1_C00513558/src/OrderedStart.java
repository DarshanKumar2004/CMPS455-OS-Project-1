public class OrderedStart {
    private static int nextId = 0;
    private static final Lock lock = new Lock();

    public void waitArrival(int philosopherId) throws InterruptedException {
        while (true) {
            lock.lock();

            try {
                if (philosopherId == nextId) {
                    System.out.println("-Philosopher " + philosopherId + " starting.");
                    nextId++;
                    return;
                }
            } finally {
                lock.unlock();
            }
            Thread.yield();
        }
    }
}
