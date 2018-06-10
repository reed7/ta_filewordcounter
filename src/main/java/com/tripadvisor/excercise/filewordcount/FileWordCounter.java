package com.tripadvisor.excercise.filewordcount;

import com.tripadvisor.excercise.filewordcount.worker.BlockingExecutor;
import com.tripadvisor.excercise.filewordcount.worker.WordCounter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FileWordCounter {

    private static final Logger LOGGER = LogManager.getLogger(FileWordCounter.class);

    private static int MAX_WORKERS;
    private static long MAX_CHUNK_SIZE;
    private static long SINGLE_THREADED_VALVE = 10*1024*1024;

    static {

        MAX_WORKERS = Runtime.getRuntime().availableProcessors() * 2;

        /*
         * Max size of file allowed to be loaded into memory at the same time
         * Need to take memory usage of string conversion and split into consideration
         * while deciding the value of this setting
         */
        MAX_CHUNK_SIZE = Runtime.getRuntime().maxMemory() / 100;
    }

    private String filePath;
    private int numWorkers;
    private BlockingExecutor threadPool;

    public FileWordCounter(String filePath, int numWorkers) {
        if(filePath == null || filePath.equals("")) {
            throw new IllegalArgumentException("File path can't be empty!");
        } else {
            this.filePath = filePath;
        }

        if(numWorkers == -1) {
            this.numWorkers = MAX_WORKERS;
        } else {
            if(numWorkers > MAX_WORKERS) {
                LOGGER.info("Number of workers can't exceed 2 times of available CPUs, changing number of workers to {}",
                        MAX_WORKERS);

                this.numWorkers = MAX_WORKERS;
            } else {
                this.numWorkers = numWorkers;
            }
        }

        this.threadPool = new BlockingExecutor(this.numWorkers);

        LOGGER.info("FileWordCounter initialized with following property:" +
                "[number of workers = {}, MAX_CHUNK_SIZE = {}KB, filePath = {}]",
                this.numWorkers, MAX_CHUNK_SIZE/1000, this.filePath);
    }

    public Map<String, AtomicInteger> countWordsInFile() throws IOException {

        RandomAccessFile fileToCount = new RandomAccessFile(this.filePath, "r");
        long fileSize = fileToCount.length();

        Map<String, AtomicInteger> countResult;
        long startTs = System.currentTimeMillis();

        if(fileSize < SINGLE_THREADED_VALVE) {

            LOGGER.info("File size less than {}MB, single threaded mode used.",
                    SINGLE_THREADED_VALVE/1024/1024);

            countResult = new HashMap<>();
            Utils.countWordWithinRange(fileToCount, countResult);

        } else {

            /*
                Max size of file each thread is going to handle, unit is byte
             */
            long batchSize = Math.min(MAX_CHUNK_SIZE, fileSize) / this.numWorkers;

            long batchStart = 0;
            List<BatchRange> batches = new LinkedList<>();

            // Split file to smaller batches
            while (batchStart < fileSize) {

                long batchEnd = Math.min(fileSize - 1, batchStart + batchSize - 1);
                fileToCount.seek(batchEnd);
                // Find the nearest white space
                while (fileToCount.read() != 32 && batchEnd < fileSize - 1) {
                    batchEnd++;
                }
                batches.add(new BatchRange(batchStart, batchEnd));

                if (batchEnd == fileSize - 1) {
                    break;
                } else {
                    batchStart = batchEnd + 1;
                }
            }

            LOGGER.info("File split into {} batches. Average size of each batch is {}KB.",
                    batches.size(), batchSize / 1024 );

            countResult = new ConcurrentHashMap<>();
            try {
                for (BatchRange range : batches) {
                    try {
                        LOGGER.debug("Submitting batch [{}, {}] to the thread pool.",
                                range.getStart(), range.getEnd());

                        this.threadPool.submitTask(new WordCounter(range, this.filePath, countResult));
                    } catch (InterruptedException ie) {
                        LOGGER.error(ie.getMessage(), ie);
                    }
                }
            } finally {
                this.threadPool.shutdown();
            }
        }

        long duration = System.currentTimeMillis() - startTs;
        LOGGER.info("Counting finish in {} ms! Found {} different words on the file.",
                duration, countResult.size());
        return countResult;
    }
}
