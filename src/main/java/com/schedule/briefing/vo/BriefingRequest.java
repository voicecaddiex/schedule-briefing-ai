package com.schedule.briefing.vo;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BriefingRequest {
  private String customerId; // 고객을 식별할 고유 ID (예: 세션 ID, User ID)
  private String scheduleJson; // 전체 여행 일정 JSON 데이터 (최초 요청 시만 필요)
  private String customerQuestion; // 고객의 현재 질문

  private String language;
}
