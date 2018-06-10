package com.tripadvisor.excercise.filewordcount;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TestUtils {

    @Test
    public void testProcessIncResult() {
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        resultMap.put("abc", new AtomicInteger(9));
        resultMap.put("def", new AtomicInteger(8));
        resultMap.put("fff", new AtomicInteger(7));
        resultMap.put("sss", new AtomicInteger(6));
        resultMap.put("123", new AtomicInteger(5));

        List<Utils.WordCountResult> ret = Utils.processResult(resultMap, 4, "inc");
        Assert.assertEquals(4, ret.size(), 4);
        Assert.assertEquals("123", ret.get(0).getWord());
        Assert.assertEquals("def", ret.get(3).getWord());
    }

    @Test
    public void testProcessDecResult() {
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        resultMap.put("abc", new AtomicInteger(9));
        resultMap.put("def", new AtomicInteger(8));
        resultMap.put("fff", new AtomicInteger(7));
        resultMap.put("sss", new AtomicInteger(6));
        resultMap.put("123", new AtomicInteger(5));

        List<Utils.WordCountResult> ret = Utils.processResult(resultMap, 4, "dec");
        Assert.assertEquals(4, ret.size());
        Assert.assertEquals("abc", ret.get(0).getWord());
        Assert.assertEquals("sss", ret.get(3).getWord());
    }

    @Test
    public void testProcessTopNMoreThanResult() {
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        resultMap.put("abc", new AtomicInteger(9));
        resultMap.put("def", new AtomicInteger(8));
        resultMap.put("fff", new AtomicInteger(7));

        List<Utils.WordCountResult> ret = Utils.processResult(resultMap, 9, "inc");
        Assert.assertEquals(ret.size(), 3);
        Assert.assertEquals("fff", ret.get(0).getWord());
        Assert.assertEquals("def", ret.get(1).getWord());
    }

    @Test
    public void testProcessTopZeroOrLess() {
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        resultMap.put("abc", new AtomicInteger(9));
        resultMap.put("def", new AtomicInteger(8));
        resultMap.put("fff", new AtomicInteger(7));

        List<Utils.WordCountResult> ret = Utils.processResult(resultMap, -1, "inc");
        Assert.assertEquals(0, ret.size());
    }

    @Test
    public void testCountWord() throws Exception {
        Map<String, AtomicInteger> result = new HashMap<>();
        File file = new File("tmp");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("123 123 12'3 456 789 10".getBytes());
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            Utils.countWord(raf, result);
        }

        Assert.assertEquals(6, result.size());
        Assert.assertEquals(2, result.get("123").intValue());
        Assert.assertEquals(1, result.get("12").intValue());
        Assert.assertEquals(1, result.get("3").intValue());

        file.delete();
    }

    @Test
    public void testCountWordWithRange() throws Exception {
        Map<String, AtomicInteger> result = new HashMap<>();
        File file = new File("tmp");
        BatchRange range = new BatchRange(3, 18);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("123 123 456 789 456 456 10 222 123".getBytes());
            RandomAccessFile raf = new RandomAccessFile(file, "r");

            Utils.countWord(range, raf, result);
        }

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(1, result.get("123").intValue());
        Assert.assertEquals(2, result.get("456").intValue());

        file.delete();
    }

}
