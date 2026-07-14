package com.schedule.briefing.config;

import com.schedule.briefing.entity.ScheduleOption;
import com.schedule.briefing.entity.UserSchedule;
import com.schedule.briefing.enums.PickUpTypeCd;
import com.schedule.briefing.enums.ReservationTypeCd;
import com.schedule.briefing.repository.ScheduleOptionRepository;
import com.schedule.briefing.repository.UserScheduleRepository;
import com.schedule.briefing.vo.GolfScheduleOption;
import com.schedule.briefing.vo.ScheduleOptionInfo;
import com.schedule.briefing.vo.VehicleScheduleOption;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {

  private final UserScheduleRepository userScheduleRepository;
  private final ScheduleOptionRepository scheduleOptionRepository;

  @Override
  public void run(String... args) {
    UserSchedule schedule = userScheduleRepository.save(
        UserSchedule.builder()
            .reserverName("김철수")
            .scheduleStartAt(LocalDateTime.of(2026, 8, 6, 0, 0))
            .scheduleEndAt(LocalDateTime.of(2026, 8, 9, 0, 0))
            .build()
    );

    // 공항 픽업
    scheduleOptionRepository.save(ScheduleOption.builder()
        .userScheduleId(schedule.getId())
        .reservationTypeCd(ReservationTypeCd.AIRPORT_PICK_UP)
        .reservationAt(LocalDateTime.of(2026, 8, 6, 10, 0))
        .scheduleOptionInfo(ScheduleOptionInfo.ofVehicleScheduleOption(
            VehicleScheduleOption.builder()
                .pickUpType(PickUpTypeCd.AIRPORT)
                .pickUpLocation("나리타 공항 제1터미널")
                .flightNumber("KE2129")
                .passengerCount(3)
                .totalGolfBags(3)
                .dropOffLocation("치바 컨트리클럽")
                .build()
        ))
        .build());

    // 골프장 예약
    scheduleOptionRepository.save(ScheduleOption.builder()
        .userScheduleId(schedule.getId())
        .reservationTypeCd(ReservationTypeCd.GOLF_CLUB)
        .reservationAt(LocalDateTime.of(2026, 8, 7, 8, 30))
        .scheduleOptionInfo(ScheduleOptionInfo.ofGolfScheduleOption(
            GolfScheduleOption.builder()
                .clubName("치바 컨트리클럽")
                .courseName("동코스")
                .playerList(List.of("김철수", "이영희"))
                .isLunchProvided(true)
                .build()
        ))
        .build());
  }
}
