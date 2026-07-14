package com.schedule.briefing.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GeminiBriefingResponse {

  private String response;

  private String error;

  private String detail;
}
