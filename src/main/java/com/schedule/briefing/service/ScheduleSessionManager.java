package com.schedule.briefing.service;

import com.google.genai.Chat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ScheduleSessionManager {

  // Value: 해당 고객의 현재 Chat 객체 (멀티턴 대화를 위한 세션 객체)
  private final Map<String, Chat> activeSessions = new ConcurrentHashMap<>();

  public void putSession(String customerId, Chat newChat) {
    activeSessions.put(customerId, newChat);
  }

  public Chat getSession(String customerId) {
    return activeSessions.get(customerId);
  }

  public void removeSession(String customerId) {
    // 실제로는 세션 종료 시점에 GC 대상이 되도록 Map에서만 제거하면 됩니다.
    activeSessions.remove(customerId);
  }
}
