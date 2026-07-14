package com.schedule.briefing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationTypeCd {

  AIRPORT_PICK_UP("공항 픽업"),
  AIRPORT_SENDING("공항 샌딩"),
  GOLF_COURSE_PICK_UP("골프장 픽업"),
  GOLF_COURSE_SENDING("골프장 샌딩"),
  GOLF_CLUB("골프장 예약"),
  ;

  private final String description;
}
