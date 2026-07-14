package com.schedule.briefing.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {

  public static Long convertLocalDateTimeToEpochSecond(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return null;
    }
    // 시간의 time zone 을 asia/seoul로 설정
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Seoul"));
    return zonedDateTime.toEpochSecond(); // Epoch 초로 변환
  }


  /**
   * 에포크 초를 LocalDateTime으로 컨버트
   * @param epochSecond 에포크 초
   * @return LocalDateTime
   */
  public static LocalDateTime convertEpochSecondToLocalDateTime(Long epochSecond) {
    if (epochSecond == null) {
      return null;
    }

    // 초 단위와 밀리초 단위의 기준을 비교하여 확인 (예: 1_000_000_000_000 밀리초는 약 31.7년)
    if (epochSecond >= 1_000_000_000_000L) {
      throw new IllegalArgumentException("Input value is in milliseconds, not seconds");
    }

    return LocalDateTime.ofInstant(
        Instant.ofEpochSecond(epochSecond),
        ZoneId.of("Asia/Seoul"));
  }
}
