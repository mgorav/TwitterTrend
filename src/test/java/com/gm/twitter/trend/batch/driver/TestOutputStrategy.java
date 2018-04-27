package com.gm.twitter.trend.batch.driver;

import com.gm.twitter.trend.batch.model.TweetWindow;
import lombok.Getter;
import scala.Tuple2;

import java.util.LinkedList;
import java.util.List;

@Getter
public class TestOutputStrategy implements OutputStrategy{

    private List<Tuple2<Integer, String>> trends = new LinkedList<>();
    private List<TweetWindow> windows = new LinkedList<>();

    @Override
    public void write(Tuple2<Integer, String> output) {
         trends.add(output);
    }

    @Override
    public void write(TweetWindow tweetWindow) {
        windows.add(tweetWindow);
    }
}
