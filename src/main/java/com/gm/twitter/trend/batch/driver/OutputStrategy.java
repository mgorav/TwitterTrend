package com.gm.twitter.trend.batch.driver;

import com.gm.twitter.trend.batch.model.TweetWindow;
import scala.Tuple2;

/**
 * The output strategy to produce the output of {@link TwitterTrend}
 */
public interface OutputStrategy {

    default void write(Tuple2<Integer,String> output) {
        System.out.println(output);
    }
    default void write(TweetWindow tweetWindow) {
        System.out.println(tweetWindow);
    }
}

/**
 * Console based output strategy
 */
class ConsoleOutput implements OutputStrategy {
    // will choose default strategy
}
