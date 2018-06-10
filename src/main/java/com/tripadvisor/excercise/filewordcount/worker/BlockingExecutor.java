package com.tripadvisor.excercise.filewordcount.worker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;

/**
 * A blocking thread pool which will block the caller while number of tasks in the pool
 * has reached double of the pool size until new thread is available
 */
public class BlockingExecutor {

    private static final Logger LOGGER = LogManager.getLogger(BlockingExecutor.class);

    private final ExecutorService executor;
    private final Semaphore semaphore;

    public BlockingExecutor(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.semaphore = new Semaphore(poolSize*2);
    }

    public void submitTask(final Runnable task)
            throws InterruptedException {
        this.semaphore.acquire();

        this.executor.submit(() -> {
            try {
                task.run();
            } finally {
                semaphore.release();
            }
        });
    }

    public void shutdown() {
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
