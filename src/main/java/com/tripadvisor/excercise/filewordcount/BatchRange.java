package com.tripadvisor.excercise.filewordcount;

public class BatchRange {

    private long start, end;

    public BatchRange(long start, long end) {
        if(start > end) {
            throw new IllegalArgumentException("End index must greater than start index!");
        }

        this.start = start;
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

}
