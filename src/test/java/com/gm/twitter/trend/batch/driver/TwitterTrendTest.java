package com.gm.twitter.trend.batch.driver;

import com.gm.twitter.trend.batch.model.TweetWindow;
import com.gm.twitter.trend.batch.model.UnitOfTime;
import com.google.common.collect.Lists;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Paths.get;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TwitterTrendTest {

    private TwitterTrend twitterTrend = new TwitterTrend();
    private Broadcast<List<String>> stopWords;
    private Broadcast<Tuple2<Integer, UnitOfTime>> arguments;
    private JavaSparkContext sc;
    private TestOutputStrategy output;

    @Before
    public void before() throws IOException {
        SparkConf conf = new SparkConf().setAppName("TwitterTrend").setMaster("local[1]");

        sc = new JavaSparkContext(conf);
        stopWords = sc.broadcast(readAllLines(get("in/Ignorewords.txt")));
        arguments = sc.broadcast(new Tuple2<>(2, UnitOfTime.DAY));
        output = new TestOutputStrategy();

    }

    @After
    public void after() throws IOException {
        sc.close();
    }



    @Test
    public void testProcessLines() {
        JavaRDD<String> lines = sc.parallelize(Lists.newArrayList("{\"text\": \"simple Text about trends in Twitter\",\"id\": \"12\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}",
                "{\"text\": \"Not so simple text about trends in Twitter\",\"id\": \"23\",\"created_at\": \"Sat Feb 26 14:05:33 +0000 2011\"}",
                "{\"text\": \"Very simple text on trends\",\"id\": \"34\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}",
                "{\"text\": \"5 times simple words on trends\",\"id\": \"45\",\"created_at\": \"Sat Feb 26 14:05:33 +0000 2011\"}"));
        twitterTrend.processLines(lines, stopWords, arguments, output);
        assertThat("Output has 2 windows", output.getWindows().size(), is(2));
        //2 for each window
        assertThat("Output has 4 trends", output.getTrends().size(), is(4));

        //first window
        TweetWindow window1 = output.getWindows().get(0);
        Tuple2<Integer, String> trend11 = output.getTrends().get(0);
        Tuple2<Integer, String> trend12 = output.getTrends().get(1);

        assertThat(window1.getStartWindow(), is(LocalDateTime.of(2011, 02, 25, 0, 0)));
        assertThat(window1.getEndWindow(), is(LocalDateTime.of(2011, 02, 26, 0, 0)));

        assertThat(trend11._2, anyOf(is("simple"), is("trends")));
        assertThat(trend11._1, is(2));

        assertThat(trend12._2, anyOf(is("simple"), is("trends")));
        assertThat(trend12._1, is(2));

        //second window
        TweetWindow window2 = output.getWindows().get(1);
        Tuple2<Integer, String> trend21 = output.getTrends().get(2);
        Tuple2<Integer, String> trend22 = output.getTrends().get(3);

        assertThat(window2.getStartWindow(), is(LocalDateTime.of(2011, 02, 26, 0, 0)));
        assertThat(window2.getEndWindow(), is(LocalDateTime.of(2011, 02, 27, 0, 0)));

        assertThat(trend21._2, anyOf(is("simple"), is("trends")));
        assertThat(trend21._1, is(2));

        assertThat(trend22._2, anyOf(is("simple"), is("trends")));
        assertThat(trend22._1, is(2));
    }

    @Test
    public void testProcessLinesTop2() {
        JavaRDD<String> lines = sc.parallelize(Lists.newArrayList("{\"text\": \"friends like ice cream\",\"id\": \"12\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}",
                "{\"text\": \"party is on the mind with friends\",\"id\": \"23\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}",
                "{\"text\": \"i love my friends\",\"id\": \"34\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}",
                "{\"text\": \"friends and party go together like ice cream and cone. love love my friends\",\"id\": \"45\",\"created_at\": \"Fri Feb 25 14:05:33 +0000 2011\"}"));
        twitterTrend.processLines(lines, stopWords, arguments, output);
        assertThat("Output has 1 windows", output.getWindows().size(), is(1));
        //2 for each window
        assertThat("Output has 2 trends", output.getTrends().size(), is(2));

        //first window
        TweetWindow window1 = output.getWindows().get(0);
        Tuple2<Integer, String> trend11 = output.getTrends().get(0);
        Tuple2<Integer, String> trend12 = output.getTrends().get(1);

        assertThat(window1.getStartWindow(), is(LocalDateTime.of(2011, 02, 25, 0, 0)));
        assertThat(window1.getEndWindow(), is(LocalDateTime.of(2011, 02, 26, 0, 0)));

        assertThat(trend11._2, anyOf(is("friends"), is("love")));
        assertThat(trend11._1, trend11._2.equalsIgnoreCase("friends") ? is(5) : is(3));

        assertThat(trend12._2, anyOf(is("friends"), is("love")));
        assertThat(trend12._1, trend12._2.equalsIgnoreCase("friends") ? is(5) : is(3));

    }


}
