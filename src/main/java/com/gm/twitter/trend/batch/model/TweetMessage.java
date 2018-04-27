package com.gm.twitter.trend.batch.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gm.twitter.trend.batch.constants.TwitterTrendConstants;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.parse;

/**
 * A representation of a twitter message
 */
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
@Getter
public class TweetMessage implements Serializable {


    final private String id;
    final private String text;
    final private TweetWindow tweetWindow;


    public static TweetMessage newTweetMessageForGivenUnitOfTime(String line, ObjectMapper mapper, UnitOfTime unitOfTime) {
        TweetMessage tweetMessage;
        String idStr = null;
        String text = null;
        LocalDateTime createdAt = null;
        try {
            JsonNode jsonNode = mapper.readTree(line);
            jsonNode = jsonNode.has("twitter") ? jsonNode.get("twitter") : jsonNode;
            idStr = jsonNode.get("id").asText();
            text = jsonNode.get("text").asText();
            String createdAtStr = jsonNode.get("created_at").asText();
            createdAt = parse(createdAtStr, createdAtStr.contains(",") ? TwitterTrendConstants.DATE_FORMATTER_TWITTER : TwitterTrendConstants.DATE_FORMATTER);

        } catch (Exception e) {
            // Skip all the messages other than StatusMessage
        }

        if (text == null || createdAt == null) {
            return null;
        }

        return new TweetMessage(idStr, text, TweetWindow.toWindow(createdAt, unitOfTime));
    }

    public TweetMessage copyWithThisText(String text) {
        return new TweetMessage(id, text, tweetWindow);
    }


}
