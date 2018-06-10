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

    public static void countWord(RandomAccessFile fileToCount,
                                 Map<String, AtomicInteger> result) throws IOException {
        long start = 0;
        long end = fileToCount.length();

        countWord(new BatchRange(start, end), fileToCount, result);
    }

    public static void countWord(BatchRange range, RandomAccessFile fileToCount,
                                 Map<String, AtomicInteger> result) throws IOException {
        long start = range.getStart();
        long end = range.getEnd();

        LOGGER.debug("Counting word for range [{}, {}]", start, end);

        fileToCount.seek(start);

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

    public static List<WordCountResult> processResult(Map<String, AtomicInteger> result, int topN, final String order) {
        PriorityQueue<WordCountResult> pq = new PriorityQueue<>(
                (r1, r2) -> {
                    if("dec".equals(order)){
                        return r1.count - r2.count;
                    } else {
                        return r2.count - r1.count;
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

    static class WordCountResult {
        private String word;
        private int count;

        WordCountResult(String word, int count) {
            this.word = word;
            this.count = count;
        }

        public String getWord(){
            return word;
        }

        public int getCount() {
            return count;
        }
    }

}
