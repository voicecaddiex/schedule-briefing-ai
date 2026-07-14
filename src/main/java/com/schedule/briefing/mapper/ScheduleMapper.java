package com.schedule.briefing.mapper;

import com.schedule.briefing.dto.ScheduleResponse;
import com.schedule.briefing.dto.ScheduleResponse.GolfOptionResponse;
import com.schedule.briefing.dto.ScheduleResponse.ScheduleItem;
import com.schedule.briefing.dto.ScheduleResponse.ScheduleOptionInfoResponse;
import com.schedule.briefing.dto.ScheduleResponse.VehicleOptionResponse;
import com.schedule.briefing.entity.ScheduleOption;
import com.schedule.briefing.entity.UserSchedule;
import com.schedule.briefing.util.TimeUtil;
import com.schedule.briefing.vo.GolfScheduleOption;
import com.schedule.briefing.vo.ScheduleOptionInfo;
import com.schedule.briefing.vo.VehicleScheduleOption;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

  public ScheduleResponse toResponse(UserSchedule schedule, List<ScheduleOption> options) {
    return ScheduleResponse.builder()
        .scheduleStartAt(schedule.getScheduleStartAt())
        .scheduleEndAt(schedule.getScheduleEndAt())
        .scheduleList(options.stream()
            .map(this::toScheduleItem)
            .collect(Collectors.toList()))
        .build();
  }

  private ScheduleItem toScheduleItem(ScheduleOption option) {
    return ScheduleItem.builder()
        .scheduleOptionId(option.getScheduleOptionId())
        .reservationTypeCd(option.getReservationTypeCd().name())
        .reservationAt(option.getReservationAt())
        .scheduleOptionInfo(toResponseInfo(option.getScheduleOptionInfo()))
        .build();
  }

  private ScheduleOptionInfoResponse toResponseInfo(ScheduleOptionInfo info) {
    if (info == null) return null;

    VehicleOptionResponse vehicleRes = null;
    GolfOptionResponse golfRes = null;

    if (info.getVehicleScheduleOption() != null) {
      VehicleScheduleOption origin = info.getVehicleScheduleOption();
      vehicleRes = VehicleOptionResponse.builder()
          .pickUpLocation(origin.getPickUpLocation())
          .pickUpLocationEng(origin.getPickUpLocationEng())
          .pickUpDateTime(TimeUtil.convertEpochSecondToLocalDateTime(origin.getPickUpDateTime()))
          .pickUpAddress(origin.getPickUpAddress())
          .pickUpLatitude(origin.getPickUpLatitude())
          .pickUpLongitude(origin.getPickUpLongitude())
          .dropOffLocation(origin.getDropOffLocation())
          .dropOffLocationEng(origin.getDropOffLocationEng())
          .dropOffAddress(origin.getDropOffAddress())
          .dropOffLatitude(origin.getDropOffLatitude())
          .dropOffLongitude(origin.getDropOffLongitude())
          .passengerCount(origin.getPassengerCount())
          .totalCarryOnCarrier(origin.getTotalCarryOnCarrier())
          .totalCheckedCarrier(origin.getTotalCheckedCarrier())
          .totalGolfBags(origin.getTotalGolfBags())
          .flightNumber(origin.getFlightNumber())
          .flightArrivalTime(TimeUtil.convertEpochSecondToLocalDateTime(origin.getFlightArrivalTime()))
          .originAirport(origin.getOriginAirport())
          .originAirportBoardingTime(TimeUtil.convertEpochSecondToLocalDateTime(origin.getOriginAirportBoardingTime()))
          .processingTime(origin.getProcessingTime())
          .teeOffDateTime(TimeUtil.convertEpochSecondToLocalDateTime(origin.getTeeOffDateTime()))
          .build();
    }

    if (info.getGolfScheduleOption() != null) {
      GolfScheduleOption origin = info.getGolfScheduleOption();
      golfRes = GolfOptionResponse.builder()
          .clubName(origin.getClubName())
          .courseName(origin.getCourseName())
          .reservationTime(TimeUtil.convertEpochSecondToLocalDateTime(origin.getReservationTime()))
          .playerList(origin.getPlayerList())
          .isLunchProvided(origin.getIsLunchProvided())
          .build();
    }

    return ScheduleOptionInfoResponse.builder()
        .vehicleScheduleOption(vehicleRes)
        .golfScheduleOption(golfRes)
        .build();
  }
}
