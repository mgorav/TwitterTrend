package com.gm.twitter.trend.batch.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.gm.twitter.trend.batch.model.TweetMessage.newTweetMessageForGivenUnitOfTime;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TweetMessageTest {

    @Test
    public void testNewTweetMessageNotCorrectFormat() {

        TweetMessage tweetMessage = newTweetMessageForGivenUnitOfTime("Incorrect Format", new ObjectMapper(), UnitOfTime.DAY);
        assertThat("TweetMessage should be null as the format is not what we expect", tweetMessage, nullValue());

    }

    @Test
    public void testNewTweetMessageCorrectFormat() {

        TweetMessage tweetMessage = newTweetMessageForGivenUnitOfTime("{\"user\":{\"listed_count\":12,\"following\":null,\"profile_background_image_url\":\"http:\\/\\/a2.twimg.com\\/profile_background_images\\/194723794\\/OgAAAJpn-7-9FPOJMxTRqt42Lis_Q1PnCW3Fd_RSKdMcer7W9m4BVnejhCWw0nIF-oLabnOkogszvmzUMJZeCGrAXPkAm1T1UHMEB9vyKMUYCESTClEzwNU6r7mB.jpg\",\"favourites_count\":5,\"followers_count\":1264,\"contributors_enabled\":false,\"statuses_count\":3243,\"time_zone\":\"Brasilia\",\"profile_text_color\":\"0C3E53\",\"friends_count\":2001,\"profile_sidebar_fill_color\":\"FFF7CC\",\"id_str\":\"73177503\",\"profile_background_tile\":true,\"profile_image_url\":\"http:\\/\\/a2.twimg.com\\/profile_images\\/1246186705\\/173827_100000148497037_3423690_q_normal.jpg\",\"created_at\":\"Thu Sep 10 17:58:14 +0000 2009\",\"description\":\"CEARENSE q mora no interior do maranh\\u00e3o, q trabalha no setor financeiro de uma loja de auto pe\\u00e7as, q procura ser feliz no amor\",\"notifications\":null,\"profile_link_color\":\"FF0000\",\"location\":\"BACABAL,MA\",\"screen_name\":\"shennama\",\"follow_request_sent\":null,\"profile_sidebar_border_color\":\"F2E195\",\"protected\":false,\"lang\":\"en\",\"verified\":false,\"name\":\"CREUSA DA COSTA\",\"profile_use_background_image\":true,\"id\":73177503,\"is_translator\":false,\"show_all_inline_media\":false,\"geo_enabled\":false,\"utc_offset\":-10800,\"profile_background_color\":\"BADFCD\",\"url\":null},\"in_reply_to_screen_name\":null,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"contributors\":null,\"retweeted\":false,\"text\":\"Valid tweet text\",\"in_reply_to_user_id_str\":null,\"retweet_count\":0,\"id_str\":\"41136747991535616\",\"source\":\"web\",\"geo\":null,\"created_at\":\"Fri Feb 25 14:05:39 +0000 2011\",\"truncated\":false,\"entities\":{\"user_mentions\":[{\"indices\":[20,33],\"id_str\":\"62903186\",\"screen_name\":\"Capoeiristaa\",\"name\":\"Thiago\",\"id\":62903186}],\"urls\":[],\"hashtags\":[]},\"place\":null,\"favorited\":false,\"id\":41136747991535616,\"coordinates\":null}\n", new ObjectMapper(), UnitOfTime.DAY);
        assertThat
                ("TweetMessage should not be null as the format is not what we expect", tweetMessage, notNullValue());
        assertThat(tweetMessage.getText(), is("Valid tweet text"));
        assertThat(tweetMessage.getTweetWindow().getStartWindow(), is(LocalDateTime.of(2011, 02, 25, 0, 0))) ;
        assertThat(tweetMessage.getTweetWindow().getEndWindow(), is(LocalDateTime.of(2011, 02, 26, 0, 0))) ;

    }

    @Test
    public void testNewTweetMessageCorrectFormatLargeDataSet() {

        TweetMessage tweetMessage = newTweetMessageForGivenUnitOfTime("{\"interaction\":{\"author\":{\"avatar\":\"http://a2.twimg.com/profile_images/1782895519/loveyoux_normal.jpg\",\"id\":378562180,\"link\":\"http://twitter.com/chey1309\",\"name\":\"Cheyenne♥Martijnx\",\"username\":\"chey1309\"},\"content\":\"Valid tweet text large\",\"created_at\":\"Sat, 04 Feb 2012 09:38:03 +0000\",\"id\":\"1e14f13eef78af80e074f4e161ddc68e\",\"link\":\"http://twitter.com/chey1309/statuses/165730828415025152\",\"source\":\"web\",\"type\":\"twitter\"},\"language\":{\"tag\":\"nl\"},\"twitter\":{\"created_at\":\"Sat, 04 Feb 2012 09:38:03 +0000\",\"id\":\"165730828415025152\",\"mentions\":[\"FALLONIUSS\"],\"source\":\"web\",\"text\":\"Valid tweet text large\",\"user\":{\"description\":\"18♥01♥12\",\"followers_count\":97,\"friends_count\":181,\"id\":378562180,\"id_str\":\"378562180\",\"lang\":\"nl\",\"location\":\"in jouw hartjee ♥\",\"name\":\"Cheyenne♥Martijnx\",\"screen_name\":\"chey1309\",\"statuses_count\":5313,\"time_zone\":\"Greenland\",\"url\":\"http://www.love1309.hyves.nl\"}}}\n", new ObjectMapper(),UnitOfTime.DAY);
         assertThat("TweetMessage should not be null as the format is not what we expect", tweetMessage, notNullValue());
        assertThat(tweetMessage.getText(), is("Valid tweet text large"));
        assertThat(tweetMessage.getTweetWindow().getStartWindow(), is(LocalDateTime.of(2012, 02, 4, 0, 0))) ;
        assertThat(tweetMessage.getTweetWindow().getEndWindow(), is(LocalDateTime.of(2012, 02, 5, 0, 0))) ;

    }

}
