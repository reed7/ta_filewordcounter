package com.tripadvisor.excercise.filewordcount;

public class WordCountResult {

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
