package com.gm.twitter.trend.batch.model;

import com.gm.twitter.trend.batch.constants.TwitterTrendConstants;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.gm.twitter.trend.batch.model.UnitOfTime.*;
import static com.gm.twitter.trend.batch.model.TweetWindow.toWindow;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TweetWindowTest {

    @Test
    public void testCreateWindowPerDay() {
        LocalDateTime time = getLocalDateTime("Fri Nov 26 15:23:48 +0000 2010");
        TweetWindow tweetWindow = toWindow(time, DAY);
        assertThat("Start Time of tweetWindow is",
                tweetWindow.getStartWindow(), is(getLocalDateTime("Fri Nov 26 00:00:00 +0000 2010")));
        assertThat("End Time of tweetWindow is",
                tweetWindow.getEndWindow(), is(getLocalDateTime("Sat Nov 27 00:00:00 +0000 2010")));
    }

    @Test
    public void testCreateWindowPerDayEndOfMonth() {
        LocalDateTime time = getLocalDateTime("Tue Nov 30 15:23:48 +0000 2010");
        TweetWindow tweetWindow = toWindow(time, DAY);
        assertThat("Start Time of tweetWindow is",
                tweetWindow.getStartWindow(), is(getLocalDateTime("Tue Nov 30 00:00:00 +0000 2010")));
        assertThat("End Time of tweetWindow is",
                tweetWindow.getEndWindow(), is(getLocalDateTime("Wed Dec 01 00:00:00 +0000 2010")));
    }

    @Test
    public void testCreateWindowPerDayFebLeapYear() {
        LocalDateTime time = getLocalDateTime("Sun Feb 28 15:23:48 +0000 2016");
        TweetWindow tweetWindow = toWindow(time, DAY);
        assertThat("Start Time of tweetWindow is",
                tweetWindow.getStartWindow(), is(getLocalDateTime("Sun Feb 28 00:00:00 +0000 2016")));
        assertThat("End Time of tweetWindow is",
                tweetWindow.getEndWindow(), is(getLocalDateTime("Mon Feb 29 00:00:00 +0000 2016")));
    }

    @Test
    public void testCreateWindowPerMinute() {
        LocalDateTime time = getLocalDateTime("Sun Feb 28 15:23:48 +0000 2016");
        TweetWindow tweetWindow = toWindow(time, MINUTE);
        assertThat("Start Time of tweetWindow is",
                tweetWindow.getStartWindow(), is(getLocalDateTime("Sun Feb 28 15:23:00 +0000 2016")));
        assertThat("End Time of tweetWindow is",
                tweetWindow.getEndWindow(), is(getLocalDateTime("Sun Feb 28 15:24:00 +0000 2016")));
    }

    @Test
    public void testCreateWindowPerHour() {
        LocalDateTime time = getLocalDateTime("Sun Feb 28 15:23:48 +0000 2016");
        TweetWindow tweetWindow = toWindow(time, HOUR);
        assertThat("Start Time of tweetWindow is",
                tweetWindow.getStartWindow(), is(getLocalDateTime("Sun Feb 28 15:00:00 +0000 2016")));
        assertThat("End Time of tweetWindow is",
                tweetWindow.getEndWindow(), is(getLocalDateTime("Sun Feb 28 16:00:00 +0000 2016")));
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, TwitterTrendConstants.DATE_FORMATTER);
    }
}
