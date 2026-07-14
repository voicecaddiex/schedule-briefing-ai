package com.schedule.briefing.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.briefing.dto.ScheduleResponse;
import com.schedule.briefing.entity.ScheduleOption;
import com.schedule.briefing.entity.UserSchedule;
import com.schedule.briefing.exception.ScheduleNotFoundException;
import com.schedule.briefing.mapper.ScheduleMapper;
import com.schedule.briefing.repository.ScheduleOptionRepository;
import com.schedule.briefing.repository.UserScheduleRepository;
import com.schedule.briefing.vo.BriefingRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleBriefingService {


  private final UserScheduleRepository userScheduleRepository;
  private final ScheduleOptionRepository scheduleOptionRepository;
  private final ScheduleMapper scheduleMapper;
  private final GolfBriefingService golfBriefingService;
  private final ObjectMapper objectMapper;

  @Transactional(readOnly = true)
  public Flux<String> getScheduleChatStream(Long scheduleId, String message, String language) {
    UserSchedule schedule = userScheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new ScheduleNotFoundException(scheduleId));

    List<ScheduleOption> options = scheduleOptionRepository.findAllByUserScheduleId(schedule.getId());

    if (options.isEmpty()) {
      return Flux.error(new ScheduleNotFoundException(scheduleId));
    }

    try {
      ScheduleResponse response = scheduleMapper.toResponse(schedule, options);
      String scheduleJson = objectMapper.writeValueAsString(response);

      BriefingRequest request = BriefingRequest.builder()
          .customerId(String.valueOf(scheduleId))
          .customerQuestion(message)
          .scheduleJson(scheduleJson)
          .language(language)
          .build();

      log.info("[Briefing] Stream Start - ScheduleId: {}", scheduleId);
      return golfBriefingService.streamBriefingAnswer(request);

    } catch (Exception e) {
      log.error("[Briefing] Error preparing stream", e);
      return Flux.error(new IllegalStateException("데이터 처리 중 오류 발생", e));
    }
  }
}
