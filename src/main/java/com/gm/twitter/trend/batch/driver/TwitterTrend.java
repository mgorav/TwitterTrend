package com.gm.twitter.trend.batch.driver;

import com.gm.twitter.trend.batch.functions.TweetMessageParseFunction;
import com.gm.twitter.trend.batch.model.TweetMessage;
import com.gm.twitter.trend.batch.model.TweetWindow;
import com.gm.twitter.trend.batch.model.UnitOfTime;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;
import static org.apache.log4j.Level.ERROR;
import static org.apache.spark.storage.StorageLevel.MEMORY_ONLY;

/**
 * The main driver program to produce twitter trend. The driver class takes following arguments:
 * 1. <TOP_N> : a non negative integer to find top n topics
 * 2. <UNIT_OF_TIME>: unit of time during which the trends have to be found. The unit of time can be one
 * of MINUTE|HOUR|DAY|MONTH|YEAR
 */
@Log4j
public class TwitterTrend {

    public static void main(String[] args) throws IOException {

        Logger.getLogger("org").setLevel(ERROR);

        Tuple2<Integer, UnitOfTime> parsedArguments = verifyAndParseArguments(args);
        SparkConf conf = new SparkConf().setAppName("TwitterTrend").setMaster("local[*]");

        JavaSparkContext sc = new JavaSparkContext(conf);
        Broadcast<List<String>> stopWords = sc.broadcast(readAllLines(get("in/Ignorewords.txt")));
        Broadcast<Tuple2<Integer, UnitOfTime>> arguments = sc.broadcast(parsedArguments);

        // Step 1 > Parse the twitter JSON file & create a RDD as String
        JavaRDD<String> lines = sc.textFile("in/" + (args.length == 3 ? args[2] : "twitter.json"));
        processLines(lines, stopWords, arguments, new ConsoleOutput());

    }

    static void processLines(JavaRDD<String> lines, Broadcast<List<String>> stopWords, Broadcast<Tuple2<Integer, UnitOfTime>> arguments, OutputStrategy output) {
        // Step 2 > Creating a paired RDD > Domain Driven implementation - Convert to TweetMessage for
        // valid line in the file & apply windowing
        JavaPairRDD<TweetMessage, Integer> tweetMessagesByCount = lines.mapPartitionsToPair(
                new TweetMessageParseFunction(stopWords, arguments));

        // Step 3 >> Reduce by TweetMessage (topic) by count
        JavaPairRDD<TweetMessage, Integer> topicCount = tweetMessagesByCount.reduceByKey((x, y) -> x + y);
        // Step 4 >> Transform to topic counts per TweetWindow
        JavaPairRDD<TweetWindow, Tuple2<TweetMessage, Integer>> tweetWindowByTweetMessageCount = topicCount.mapToPair(
                tuple -> new Tuple2<>(tuple._1.getTweetWindow(), tuple));
        tweetWindowByTweetMessageCount.persist(MEMORY_ONLY());

        // In debug mod display the plan of jobs execution
        log.debug(tweetWindowByTweetMessageCount.toDebugString());


        // Step 5 >> Process topics for each window. Avoid distinct as that will result in shuffling
        Set<TweetWindow> uniqueTweetWindows = new HashSet<>();
        // Producing the result on console but same can be produced to file or some other mechanism (REST)
        for (TweetWindow tweetWindow : tweetWindowByTweetMessageCount.keys().collect()) {

            if (!uniqueTweetWindows.contains(tweetWindow)) {
                // Filter the topic by required tweetWindow
                JavaPairRDD<TweetWindow, Tuple2<TweetMessage, Integer>> filteredForThatWindow =
                        tweetWindowByTweetMessageCount.filter(tuple -> tuple._2._1.getTweetWindow().equals(tweetWindow));

                JavaPairRDD<Integer, String> topic = filteredForThatWindow.mapToPair(
                        tuple -> new Tuple2<>(tuple._2._2, tuple._2._1.getText())
                );
                output.write(tweetWindow);
                // Pick top N
                topic.sortByKey(false).take(arguments.getValue()._1).forEach(output::write);
                uniqueTweetWindows.add(tweetWindow);
            }
        }
    }

    private static Tuple2<Integer, UnitOfTime> verifyAndParseArguments(String[] args) {
        if (args.length < 2) {
            throw exception();
        }
        try {
            Integer topN = Integer.valueOf(args[0]);
            UnitOfTime unitOfTime = UnitOfTime.fromCode(args[1]);
            String nameOfDataset = "twitter.json";
            if (topN < 1 || unitOfTime == null) {
                throw exception();
            }
            return new Tuple2<>(topN, unitOfTime);
        } catch (NumberFormatException nfe) {
            throw exception();
        }

    }

    private static IllegalArgumentException exception() {
        return new IllegalArgumentException("Two arguments are expected " +
                "1. <TOP_N> : a non negative integer to find top n topics" +
                "2. <UNIT_OF_TIME>: unit of time during which the trends have to be found." +
                "                   The unit of time can be one of MINUTE|HOUR|DAY|MONTH|YEAR" +
                "3. <NAME OF THE DATASET>: OPIONAL, if not present, the system will try to find twitter.json in 'in' folder");
    }


}
