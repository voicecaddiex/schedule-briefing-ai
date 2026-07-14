package com.schedule.briefing.controller;

import com.schedule.briefing.service.ScheduleBriefingService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ScheduleBriefingController {

  private final ScheduleBriefingService scheduleBriefingService;

  @GetMapping(value = "/v1/schedule/{scheduleId}/chat", produces = "text/event-stream;charset=UTF-8")
  public ResponseEntity<SseEmitter> getScheduleChatStream(
      @PathVariable Long scheduleId,
      @RequestParam String message,
      @RequestParam(required = false, defaultValue = "ko") String language
  ) {
    SseEmitter emitter = new SseEmitter(180_000L);

    emitter.onCompletion(() -> log.info("[SSE] 완료"));
    emitter.onTimeout(() -> log.info("[SSE] 타임아웃"));
    emitter.onError(e -> log.error("[SSE] 에러: {}", e.getMessage()));

    new Thread(() -> {
      try {
        emitter.send(SseEmitter.event().name("connected").data("연결됨"));

        StringBuilder buffer = new StringBuilder();

        scheduleBriefingService.getScheduleChatStream(scheduleId, message, language)
            .doOnNext(token -> {
              try {
                buffer.append(token);
                String buf = buffer.toString();
                int sentenceEnd = findSentenceEnd(buf);

                if (sentenceEnd > 0) {
                  String sentence = buf.substring(0, sentenceEnd).trim();
                  String remaining = buf.substring(sentenceEnd);

                  if (!sentence.isEmpty()) {
                    String safeToken = sentence.replace("\r\n", "\\n").replace("\n", "\\n") + "\\n";
                    emitter.send(SseEmitter.event().name("message").data(safeToken));
                  }
                  buffer.setLength(0);
                  buffer.append(remaining);
                }
              } catch (IOException e) {
                throw new RuntimeException("SSE 전송 실패", e);
              }
            })
            .doOnComplete(() -> {
              try {
                if (!buffer.isEmpty()) {
                  String remaining = buffer.toString().trim().replace("\r\n", "\\n").replace("\n", "\\n");
                  if (!remaining.isEmpty()) {
                    emitter.send(SseEmitter.event().name("message").data(remaining + "\\n"));
                  }
                }
                emitter.send(SseEmitter.event().name("finish").data(""));
              } catch (IOException e) {
                log.error("[SSE] finish 이벤트 전송 실패", e);
              }
              emitter.complete();
            })
            .doOnError(e -> {
              log.error("[SSE] 스트림 에러", e);
              emitter.completeWithError(e);
            })
            .blockLast();

      } catch (Exception e) {
        log.error("[SSE] 에러", e);
        emitter.completeWithError(e);
      }
    }).start();

    return ResponseEntity.ok()
        .header("X-Accel-Buffering", "no")
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Connection", "keep-alive")
        .body(emitter);
  }

  private int findSentenceEnd(String text) {
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '.' || c == '!' || c == '?' || c == '\n') {
        return i + 1;
      }
    }
    return -1;
  }
}
