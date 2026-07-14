package com.schedule.briefing.repository;

import com.schedule.briefing.entity.ScheduleOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleOptionRepository extends JpaRepository<ScheduleOption, Long> {

  List<ScheduleOption> findAllByUserScheduleId(Long userScheduleId);
}