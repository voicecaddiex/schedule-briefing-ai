package com.schedule.briefing.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.ImmutableList;
import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.Client.Builder;
import com.google.genai.ResponseStream;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.Part;
import com.google.genai.types.Tool;
import com.schedule.briefing.vo.BriefingRequest;
import com.schedule.briefing.vo.GeminiBriefingRequest;
import com.schedule.briefing.vo.GeminiBriefingResponse;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class GolfBriefingService {

  private final WebClient webClient;
  private Client client;
  private final ScheduleSessionManager sessionManager;
  private final String modelId = "gemini-2.5-flash-lite";

  @Value("${gcp.vertex-ai.location}")
  private String location;
  @Value("${google.application.projectId}")
  private String projectId;
  @Value("${gcp.vertex-ai.credentials}")
  private String credentialsPath;
  @Value("${gemini.cloud-run.url}")
  private String cloudRunUrl;

  @Autowired
  public GolfBriefingService(ScheduleSessionManager sessionManager, WebClient webClient) {
    this.sessionManager = sessionManager;
    this.client = null;
    this.webClient = webClient;
  }

  @PostConstruct
  public void init() throws Exception {
    GoogleCredentials credentials;

    if (!credentialsPath.isEmpty()) {
      log.info("Vertex AI 인증: 키 파일을 직접 로드하여 설정 중...");
      try (InputStream is = new ClassPathResource(credentialsPath).getInputStream()) {
        credentials = GoogleCredentials.fromStream(is)
            .createScoped("https://www.googleapis.com/auth/cloud-platform");
      } catch (Exception e) {
        log.error("인증 키 파일 로드 중 오류 발생: {}", e.getMessage());
        throw new RuntimeException("Vertex AI 인증 실패", e);
      }
    } else {
      log.info("Vertex AI 인증: Application Default Credentials 사용");
      credentials = GoogleCredentials.getApplicationDefault();
    }

    Builder builder = Client.builder()
        .project(projectId)
        .location(location)
        .vertexAI(true);

    if (credentials != null) {
      builder.credentials(credentials);
    }

    this.client = builder.build();
  }

  private Content createSystemInstruction(String languageCode, String scheduleJson) {
    String instructionText;

    if ("en".equalsIgnoreCase(languageCode)) {
      instructionText = "You are a friendly and professional golf travel assistant. Answer based on the provided JSON itinerary.\n\n" +
          "[Critical Rules]\n" +
          "1. **Language**: Answer ONLY in **English**.\n\n" +
          "2. **How to Answer Questions**:\n" +
          "   - For weather, nearby restaurants, local attractions, or real-time information: Use Google Search Tool to search and provide accurate answers.\n" +
          "   - If the question is about something NOT in the itinerary JSON and cannot be found via search: Simply answer \"This is not included in your itinerary.\"\n" +
          "   - Always prioritize the itinerary data for schedule-related questions.\n\n" +
          "3. **Output Format (VERY IMPORTANT)**:\n" +
          "   - Use PLAIN TEXT ONLY. Never use markdown syntax.\n" +
          "   - Use a spoken, conversational style like a professional announcer.\n\n" +
          "4. **Timestamps**: Convert all Unix Timestamps in JSON to 'Date Time' format.\n\n" +
          "5. **Flight Numbers**: Output flight numbers exactly as they appear in the JSON.\n\n" +
          "6. **Mandatory Footer**: At the very end, add:\n" +
          "\"Additional Info: Contact the driver after the round for faster service.\"\n\n" +
          "[Itinerary Data]\n```json\n{INPUT_SCHEDULE_JSON}\n```";
    } else if ("ja".equalsIgnoreCase(languageCode)) {
      instructionText = "あなたは親切なゴルフ旅行アシスタントです。提供されたJSON日程に基づいて回答してください。\n\n" +
          "[重要なルール]\n" +
          "1. **言語**: 必ず**日本語**で答えてください。\n\n" +
          "2. **出力形式**: プレーンテキストのみを使用してください。\n\n" +
          "3. **必須フッター**: 回答の最後には必ず以下を付け加えてください。\n" +
          "「追加案内: ラウンド終了後、ドライバーにご連絡いただければスムーズです。」\n\n" +
          "[旅行日程データ]\n```json\n{INPUT_SCHEDULE_JSON}\n```";
    } else {
      instructionText = "당신은 '골프 여행 전문 AI 컨시어지'입니다. 제공된 여행 일정(JSON)을 바탕으로 답변하세요.\n\n" +
          "[핵심 규칙]\n" +
          "1. **언어**: 반드시 **한국어**로 답변하세요.\n\n" +
          "2. **출력 형식**: 평문(Plain Text)만 사용하세요. 마크다운 문법을 절대 사용하지 마세요.\n\n" +
          "3. **필수 안내 문구**: 답변이 끝날 때 반드시 아래 문장을 덧붙이세요.\n" +
          "\"참고로, 현지에서는 라운딩 종료 후 기사님께 연락 주시면 더 신속하게 모시겠습니다.\"\n\n" +
          "[여행 스케쥴 데이터]\n```json\n{INPUT_SCHEDULE_JSON}\n```";
    }

    String finalPrompt = instructionText.replace("{INPUT_SCHEDULE_JSON}", scheduleJson);
    return Content.fromParts(Part.fromText(finalPrompt));
  }

  public String getBriefingAnswer(String scheduleJson, String message) {
    GeminiBriefingRequest request = GeminiBriefingRequest.builder()
        .scheduleJson(scheduleJson)
        .message(message)
        .history(List.of())
        .build();

    return Objects.requireNonNull(webClient.post()
            .uri(cloudRunUrl)
            .header("Authorization", "Bearer ")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GeminiBriefingResponse.class)
            .block())
        .getResponse();
  }

  public Flux<String> streamBriefingAnswer(BriefingRequest request) {
    String customerId = request.getCustomerId();
    String customerQuestion = request.getCustomerQuestion();
    String language = request.getLanguage();

    Chat chatSession = sessionManager.getSession(customerId);

    if (chatSession == null) {
      if (request.getScheduleJson() == null || request.getScheduleJson().isEmpty()) {
        return Flux.just("여행 일정 정보가 누락되었습니다. 다시 요청해 주세요.");
      }
      return startNewChatStream(customerId, request.getScheduleJson(), customerQuestion, language);
    } else {
      return continueChatStream(chatSession, customerQuestion);
    }
  }

  private Flux<String> startNewChatStream(String customerId, String scheduleJson,
      String initialQuestion, String language) {

    Tool googleSearchTool = Tool.builder()
        .googleSearch(GoogleSearch.builder().build())
        .build();

    GenerateContentConfig config = GenerateContentConfig.builder()
        .systemInstruction(createSystemInstruction(language, scheduleJson))
        .candidateCount(1)
        .maxOutputTokens(2048)
        .tools(ImmutableList.of(googleSearchTool))
        .build();

    if (this.client == null) {
      return Flux.error(new IllegalStateException("Vertex AI Client가 초기화되지 않았습니다."));
    }

    Chat chatSession = client.chats.create(modelId, config);

    return executeStream(chatSession, initialQuestion)
        .doOnComplete(() -> {
          sessionManager.putSession(customerId, chatSession);
          log.info("새로운 세션 생성 및 저장 완료: {}", customerId);
        });
  }

  private Flux<String> executeStream(Chat chatSession, String prompt) {
    return Flux.create(sink -> {
          try {
            log.info("[Gemini] 스트림 요청 보냄! (프롬프트 길이: {})", prompt.length());
            ResponseStream<GenerateContentResponse> stream = chatSession.sendMessageStream(prompt);
            log.info("[Gemini] 스트림 객체 획득 성공. 응답 대기 중...");

            for (GenerateContentResponse resp : stream) {
              String text = resp.text();
              if (text != null) {
                sink.next(text);
              }
            }
            sink.complete();
            log.info("[Gemini] 스트림 종료 완료");
          } catch (Exception e) {
            log.error("[Gemini] 스트림 처리 중 치명적 에러!", e);
            sink.error(e);
          }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .cast(String.class);
  }

  private Flux<String> continueChatStream(Chat chatSession, String newQuestion) {
    return executeStream(chatSession, newQuestion);
  }
}