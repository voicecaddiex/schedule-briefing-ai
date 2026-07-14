package com.schedule.briefing.vo;

import com.schedule.briefing.enums.PickUpTypeCd;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VehicleScheduleOption implements Serializable {

  @Enumerated(EnumType.STRING)
  private PickUpTypeCd pickUpType;

  private String pickUpLocation;
  private String pickUpLocationEng;
  private Long pickUpDateTime;
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
  private Long flightArrivalTime;
  private String originAirport;
  private Long originAirportBoardingTime;
  private String processingTime;
  private Long teeOffDateTime;
}
