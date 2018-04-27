package com.gm.twitter.trend.batch.constants;

import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

public class TwitterTrendConstants {
    public static final DateTimeFormatter DATE_FORMATTER = ofPattern("EEE MMM dd HH:mm:ss Z yyyy");
    public static final DateTimeFormatter DATE_FORMATTER_TWITTER = ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");
}
