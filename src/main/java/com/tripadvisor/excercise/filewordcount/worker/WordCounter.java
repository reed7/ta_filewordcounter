package com.tripadvisor.excercise.filewordcount.worker;

import com.tripadvisor.excercise.filewordcount.BatchRange;
import com.tripadvisor.excercise.filewordcount.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCounter implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(WordCounter.class);

    private BatchRange range;
    private RandomAccessFile raFileToCount;
    private Map<String, AtomicInteger> countResult;

    public WordCounter(BatchRange range, String fileToCount,
                       Map<String, AtomicInteger> countResult) throws FileNotFoundException {
        this.range = range;
        this.raFileToCount = new RandomAccessFile(fileToCount, "r");
        this.countResult = countResult;
    }

    public void run() {

        try {
            long startTs = System.currentTimeMillis();
            Utils.countWord(this.range, raFileToCount, this.countResult);
            long duration = System.currentTimeMillis() - startTs;
            LOGGER.info("Range [{}, {}] finished in {} ms!",
                    range.getStart(), range.getEnd(), duration);
        } catch (IOException io) {
            LOGGER.error(io.getMessage(), io);
        }
    }

}
