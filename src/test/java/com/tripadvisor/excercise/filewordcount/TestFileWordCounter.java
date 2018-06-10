package com.tripadvisor.excercise.filewordcount;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class TestFileWordCounter {

    @Test(expected = IllegalArgumentException.class)
    public void testRejectEmptyFileName() {
        new FileWordCounter("", 0);
    }

    @Test
    public void testInitializeWorkers() throws Exception {
        FileWordCounter fw =new FileWordCounter("123", 2);

        Field numWorkersField = FileWordCounter.class.getDeclaredField("numWorkers");
        numWorkersField.setAccessible(true);
        Object val = numWorkersField.get(fw);
        Assert.assertTrue(val instanceof Integer);
        int numWorkers = (int)val;
        Assert.assertEquals(2, numWorkers);
    }

    @Test
    public void testInitializeTooManyWorkers() throws Exception {
        int cpus = Runtime.getRuntime().availableProcessors() * 2;
        FileWordCounter fw =new FileWordCounter("123", 999);

        Field numWorkersField = FileWordCounter.class.getDeclaredField("numWorkers");
        numWorkersField.setAccessible(true);
        Object val = numWorkersField.get(fw);
        Assert.assertTrue(val instanceof Integer);
        int numWorkers = (int)val;
        Assert.assertEquals(cpus, numWorkers);
    }
}
