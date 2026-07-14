package com.schedule.briefing.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
@Builder
public class ScheduleOptionInfo implements Serializable  {

  private VehicleScheduleOption vehicleScheduleOption;
  private GolfScheduleOption golfScheduleOption;

  public static ScheduleOptionInfo ofVehicleScheduleOption(VehicleScheduleOption option) {
    return ScheduleOptionInfo.builder().vehicleScheduleOption(option).build();
  }

  public static ScheduleOptionInfo ofGolfScheduleOption(GolfScheduleOption option) {
    return ScheduleOptionInfo.builder().golfScheduleOption(option).build();
  }
}
