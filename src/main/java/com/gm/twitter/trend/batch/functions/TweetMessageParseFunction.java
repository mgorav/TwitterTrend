package com.gm.twitter.trend.batch.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.twitter.trend.batch.model.TweetMessage;
import com.gm.twitter.trend.batch.model.UnitOfTime;
import com.gm.twitter.trend.batch.tokenizer.simple.SimpleTwitterTextTokenizer;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gm.twitter.trend.batch.model.TweetMessage.newTweetMessageForGivenUnitOfTime;

/**
 * This class transforms a line to a {@link TweetMessage}
 */
public class TweetMessageParseFunction implements PairFlatMapFunction<Iterator<String>, TweetMessage, Integer> {

    private Broadcast<Tuple2<Integer,UnitOfTime>> arguments;
    private Broadcast<List<String>> stopWords;

    public TweetMessageParseFunction(Broadcast<List<String>> stopWords, Broadcast<Tuple2<Integer,UnitOfTime>> arguments) {
        this.arguments = arguments;
        this.stopWords = stopWords;
    }

    @Override
    public Iterator<Tuple2<TweetMessage, Integer>> call(Iterator<String> lines) throws Exception {
        List<Tuple2<TweetMessage, Integer>> statusMessageWindows = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleTwitterTextTokenizer topicTokenizer = new SimpleTwitterTextTokenizer();
        while (lines.hasNext()) {
            String line = lines.next();
            if (!line.isEmpty()) {
                TweetMessage tweetMessage = newTweetMessageForGivenUnitOfTime(line, mapper, arguments.getValue()._2);
                if (tweetMessage != null) {
                    for (String word : topicTokenizer.tokenize(stopWords.getValue(), tweetMessage.getText())) {
                        statusMessageWindows.add(new Tuple2<>(tweetMessage.copyWithThisText(word), 1));
                    }
                }
            }
        }
        return statusMessageWindows.iterator();
    }

}
