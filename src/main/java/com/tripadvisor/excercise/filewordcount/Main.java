package com.tripadvisor.excercise.filewordcount;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final String DEFAULT_WORKERS = "-1";
    private static final String DEFAULT_TOP_WORDS = "10";
    private static final String DEFAULT_ORDER = "dec";

    public static void main(String[] args) {
        Options options = new Options();

        Option file = new Option("f", "file", true, "File to be processed");
        file.setRequired(true);
        file.setType(String.class);
        options.addOption(file);

        Option numWorker = new Option("W", "workers", true,"Optional.Number of workers. Default value is 2xCPU_cores.");
        numWorker.setRequired(false);
        numWorker.setType(Integer.class);
        options.addOption(numWorker);

        Option nTopWords = new Option("w", "words", true,"Optional.Number of top words statistics to show.Default value is 10.");
        nTopWords.setRequired(false);
        nTopWords.setType(Integer.class);
        options.addOption(nTopWords);

        Option resultOrder = new Option("o", "order", true,"Optional.Specifies the order in which results are displayed: [inc]reasing | [dec]reasing. Default value is \"dec\".");
        resultOrder.setType(String.class);
        resultOrder.setRequired(false);
        options.addOption(resultOrder);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String inputFilePath = null, order = null;
        int workers = 0, topWords = 0;
        try {
            cmd = parser.parse(options, args);

            inputFilePath = cmd.getOptionValue("file", DEFAULT_ORDER);
            try {
                workers = Integer.valueOf(cmd.getOptionValue("workers", DEFAULT_WORKERS));
                topWords = Integer.valueOf(cmd.getOptionValue("words", DEFAULT_TOP_WORDS));
            } catch (NumberFormatException nfe) {
                throw new ParseException("Value of --workers or --words need to be number!");
            }

            order = cmd.getOptionValue("order", DEFAULT_ORDER).toLowerCase();
            if(!(order.equals("inc") || order.equals("dec"))) {
                throw new ParseException("Value of --order is not valid!");
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar path/filewordcount-[version].jar [options]", options);

            System.exit(1);
        }

        try {
            Map<String, AtomicInteger> countResult =
                    new FileWordCounter(inputFilePath, workers).countWordsInFile();

            List<WordCountResult> orderedRet =
                    Utils.processResult(countResult, topWords, order);

            for(WordCountResult result : orderedRet) {
                LOGGER.info("{}: {}", result.getWord(), result.getCount());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
