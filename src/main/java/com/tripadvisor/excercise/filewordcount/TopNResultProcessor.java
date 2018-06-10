package com.tripadvisor.excercise.filewordcount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TopNResultProcessor {

    private static final Logger LOGGER = LogManager.getLogger(TopNResultProcessor.class);

    private int topN;
    private String order;

    public TopNResultProcessor(int topN, String order) {
        this.topN = topN;
        this.order = order;
    }

    public void processResult(Map<String, AtomicInteger> result) {
        PriorityQueue<WordCountResult> pq = new PriorityQueue<>(
                (r1, r2) -> {
                    if("dec".equals(this.order)){
                        return r1.count - r2.count;
                    } else {
                        return r2.count - r1.count;
                    }
                });

        for(Map.Entry<String, AtomicInteger> count : result.entrySet()) {
            pq.add(new WordCountResult(count.getKey(), count.getValue().intValue()));
            if(pq.size() > this.topN) {
                pq.poll();
            }
        }

        while(pq.size() > 0) {
            LOGGER.info("{}: {}", pq.poll().word, pq.poll().count);
        }
    }

    private class WordCountResult {
        String word;
        int count;

        WordCountResult(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }
}
