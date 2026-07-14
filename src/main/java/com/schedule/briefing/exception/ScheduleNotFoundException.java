package com.schedule.briefing.exception;

public class ScheduleNotFoundException extends RuntimeException {
  public ScheduleNotFoundException(Long scheduleId) {
    super("스케줄 정보를 찾을 수 없습니다. id : " + scheduleId);
  }
}
