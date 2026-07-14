package com.schedule.briefing.entity;

import com.schedule.briefing.converter.ScheduleOptionInfoConverter;
import com.schedule.briefing.enums.ReservationTypeCd;
import com.schedule.briefing.vo.ScheduleOptionInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "schedule_option")
public class ScheduleOption {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "schedule_option_id")
  private Long scheduleOptionId;

  @Column(name = "user_schedule_id")
  private Long userScheduleId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reservation_type")
  private ReservationTypeCd reservationTypeCd;

  @Column(name = "reservation_at")
  private LocalDateTime reservationAt;

  @Convert(converter = ScheduleOptionInfoConverter.class)
  @Column(name = "schedule_option_info", columnDefinition = "TEXT")
  private ScheduleOptionInfo scheduleOptionInfo;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
