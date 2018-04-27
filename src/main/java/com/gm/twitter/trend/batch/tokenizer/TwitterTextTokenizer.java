package com.gm.twitter.trend.batch.tokenizer;

import org.apache.spark.broadcast.Broadcast;

import java.util.List;

/**
 * Twitter Topic tokenizing strategy
 */
public interface TwitterTextTokenizer {
    List<String> tokenize(List<String> stopWords, String text);
}
