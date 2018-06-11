package com.tripadvisor.excercise.filewordcount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    /**
     * Count words on the given file.
     * @param fileToCount file object: RandomAccessFile
     * @param result count result: Map
     * @throws IOException
     */
    public static void countWord(RandomAccessFile fileToCount,
                                 Map<String, AtomicInteger> result) throws IOException {
        long start = 0;
        long end = fileToCount.length()-1;

        countWordWithinRange(new BatchRange(start, end), fileToCount, result);
    }

    /**
     * Count words on the given file within the given range.
     * @param range Range of the file, in bytes.
     * @param fileToCount file object: RandomAccessFile
     * @param result count result: Map
     * @throws IOException
     */
    public static void countWordWithinRange(BatchRange range, RandomAccessFile fileToCount,
                                            Map<String, AtomicInteger> result) throws IOException {
        long start = range.getStart();
        long end = range.getEnd();

        LOGGER.debug("Counting word for range [{}, {}]", start, end);

        fileToCount.seek(start);

        // Size of each task is controlled on the main thread,
        // so it's safe to load the whole range into the memory here
        byte[] buffer = new byte[(int)(end - start + 1)];
        fileToCount.read(buffer);

        String[] words = new String(buffer).split("\\W+");

        for(String word : words) {
            try {
                word = word.toLowerCase();

                result.putIfAbsent(word, new AtomicInteger(1)).incrementAndGet();
            } catch (NullPointerException ne) {
                LOGGER.debug("Adding new word {} into result.", word);
            }

        }
    }

    /**
     * Process the counting result based on specified ordering and number of results
     * @param result The counting result: Map
     * @param topN Number of results to return: int
     * @param order The order of the results: String
     * @return
     */
    public static List<WordCountResult> getTopNWords(Map<String, AtomicInteger> result, int topN, final String order) {
        PriorityQueue<WordCountResult> pq = new PriorityQueue<>(
                (r1, r2) -> {
                    if("dec".equals(order)){
                        return r1.getCount() - r2.getCount();
                    } else {
                        return r2.getCount() - r1.getCount();
                    }
                });

        for(Map.Entry<String, AtomicInteger> count : result.entrySet()) {
            pq.add(new WordCountResult(count.getKey(), count.getValue().intValue()));
            if(pq.size() > topN) {
                pq.poll();
            }
        }

        List<WordCountResult> orderedRet = new ArrayList<>();
        while(pq.size() > 0) {
            orderedRet.add(0, pq.poll());
        }

        return orderedRet;
    }
}
