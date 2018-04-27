package com.gm.twitter.trend.batch.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TweetWindow implements Serializable {

    final private LocalDateTime startWindow;
    final private LocalDateTime endWindow;

    public static TweetWindow toWindow(LocalDateTime dateTime, UnitOfTime unitOfTime) {
        LocalDateTime startTime = dateTime;
        LocalDateTime endTime;
        switch (unitOfTime) {
            case MINUTE: {
                startTime = startTime.withSecond(0);
                endTime = startTime.plus(Duration.ofMinutes(1));
                break;
            }
            case HOUR: {
                startTime = startTime.withSecond(0).withMinute(0);
                endTime = startTime.plus(Duration.ofHours(1));
                break;
            }
            case DAY: {
                startTime = startTime.withSecond(0).withMinute(0).withHour(0);
                endTime = startTime.plus(Duration.ofDays(1));
                break;
            }
            case MONTH: {
                startTime = startTime.withSecond(0).withMinute(0).withHour(0).withDayOfMonth(1);
                endTime = startTime.withDayOfMonth(startTime.getMonth().length(startTime.getYear() % 4 == 0));
                break;
            }
            case YEAR: {
                startTime = startTime.withSecond(0).withMinute(0).withHour(0).withMonth(1).withDayOfMonth(1);
                endTime = startTime.withMonth(12).withDayOfMonth(31);
                break;
            }
            default: {
                throw new IllegalArgumentException("Not a valid unit of time");
            }
        }
        return new TweetWindow(startTime, endTime);
    }


}
