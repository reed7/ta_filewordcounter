package com.tripadvisor.excercise.filewordcount;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static void main(String[] args) throws Exception {


        String[] ret = " 123 456 789 456 ".split("\\W+");
        System.out.println(ret[0].getBytes());

        /*
        long worker = (long)(Runtime.getRuntime().availableProcessors() * 0.8 * 5);
        long sectorLen = length/worker;

        List<long[]> li = new LinkedList<>();
        long start = 0;

        while(true) {
            long currEnd = Math.min(length-1, start+sectorLen-1);

            if(currEnd == length-1) {
                li.add(new long[]{start, currEnd});
                break;
            } else {
                raf.seek(currEnd);
                while (raf.readByte() != 32 && currEnd < length - 1) {
                    raf.seek(++currEnd);
                }

                li.add(new long[]{start, currEnd});
                start = currEnd + 1;
            }
        }

        try(FileOutputStream fos = new FileOutputStream("/Users/wzhong/Desktop/TA/movies32.txt")) {
            raf.seek(li.get(31)[0]);
            byte[] buffer = new byte[(int) (li.get(31)[1] - li.get(31)[0])];
            raf.read(buffer);
            fos.write(buffer);
        }*/

        /*
        for(long[] subLi : li) {
            long subStart = subLi[0];
            long subEnd = subLi[1];

            byte[] buffer = new byte[(int)(subEnd - subStart+1)];
            raf.read(buffer);
            System.out.println(new String(buffer));
            System.out.println();
        }*/


    }

    static class FileReader implements Callable<String> {

        private RandomAccessFile raf;
        private long start, end;

        FileReader(RandomAccessFile raf, int start, int len) {
            this.raf = raf;
            this.start = start;
            try {
                this.end = Math.min(start + len, raf.length());
            } catch (IOException io) {
                this.end = start + len;
            }
        }

        public String call() {
            StringBuilder sb = new StringBuilder();
            try {
                raf.seek(this.start);
                while(raf.getFilePointer() < this.end) {
                    sb.append(raf.readChar());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return sb.toString();
        }
    }
}
