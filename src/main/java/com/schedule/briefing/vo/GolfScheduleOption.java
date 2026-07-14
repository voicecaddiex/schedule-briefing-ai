package com.schedule.briefing.vo;

import java.io.Serializable;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GolfScheduleOption implements Serializable {

  private String clubName;
  private String courseName;
  private Long reservationTime;
  private List<String> playerList;
  private Boolean isLunchProvided;
}
