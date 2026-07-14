package com.schedule.briefing.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponse {

  private LocalDateTime scheduleStartAt;
  private LocalDateTime scheduleEndAt;
  private List<ScheduleItem> scheduleList;

  @Getter
  @Builder
  public static class ScheduleItem {
    private Long scheduleOptionId;
    private String reservationTypeCd;
    private LocalDateTime reservationAt;
    private ScheduleOptionInfoResponse scheduleOptionInfo;
  }

  @Getter
  @Builder
  public static class ScheduleOptionInfoResponse {
    private VehicleOptionResponse vehicleScheduleOption;
    private GolfOptionResponse golfScheduleOption;
  }

  @Getter
  @Builder
  public static class VehicleOptionResponse {
    private String pickUpLocation;
    private String pickUpLocationEng;
    private LocalDateTime pickUpDateTime;
    private String pickUpAddress;
    private Double pickUpLatitude;
    private Double pickUpLongitude;
    private String dropOffLocation;
    private String dropOffLocationEng;
    private String dropOffAddress;
    private Double dropOffLatitude;
    private Double dropOffLongitude;
    private Integer passengerCount;
    private Integer totalCarryOnCarrier;
    private Integer totalCheckedCarrier;
    private Integer totalGolfBags;
    private String flightNumber;
    private LocalDateTime flightArrivalTime;
    private String originAirport;
    private LocalDateTime originAirportBoardingTime;
    private String processingTime;
    private LocalDateTime teeOffDateTime;
  }

  @Getter
  @Builder
  public static class GolfOptionResponse {
    private String clubName;
    private String courseName;
    private LocalDateTime reservationTime;
    private List<String> playerList;
    private Boolean isLunchProvided;
  }
}
