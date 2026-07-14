package com.schedule.briefing.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GeminiBriefingRequest {

  @JsonProperty("schedule_json")
  private String scheduleJson;

  private String message;

  private List<Object> history;

}
