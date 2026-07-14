# Schedule Briefing AI

AI 에이전트가 골프 여행 일정(픽업, 티오프 시간, 항공편)을 자연어로 브리핑해주는 챗봇 API입니다.

## 배경

실무에서 만든 골프 여행 스케줄 브리핑 서비스의 핵심 로직을, 회사 인프라와 분리된
독립 프로젝트로 재구현했습니다. 실 서비스 데이터/인증 정보는 전혀 포함하지 않으며,
목업 데이터로 동작을 확인할 수 있습니다.

## 핵심 기능
- **실시간 스트리밍**: 문장 단위로 답변을 끊어 보내는 커스텀 버퍼링 로직 (SSE)
- **멀티턴 대화**: 세션 기반 Chat 객체 관리로 후속 질문 처리
- **다국어 프롬프트**: 한국어/영어/일본어 시스템 프롬프트 분기
- **Google Search Grounding**: 날씨, 주변 정보 등 실시간 질문 처리
- **스케줄 데이터 모델링**: 항공/골프 옵션을 다형적으로 담는 JSON 컬럼 설계 (`ScheduleOptionInfo` + 커스텀 컨버터)

## 기술 스택

- Java 17 / Spring Boot 3.5
- Spring Data JPA + H2 (인메모리 DB)
- Spring WebFlux (WebClient, Reactor)
- Google Gen AI SDK (Vertex AI / Gemini)
- Gradle

## 실행 방법

1. `src/main/resources/application-local.yaml.example`을 참고해 `application-local.yaml` 생성
2. 본인의 GCP 프로젝트에서 Vertex AI 서비스 계정 키를 발급받아 `src/main/resources/`에 저장하고,
   `application-local.yaml`의 `credentials` 값에 해당 파일명을 지정
3. 실행

```bash
./gradlew bootRun
```

4. H2 콘솔(`http://localhost:8080/h2-console`)에서 목업 데이터 확인 가능

## API 사용 예시

```bash
curl -N "http://localhost:8080/v1/schedule/1/chat?message=<질문 내용>&language=ko"
```

예: `message`에 "내일 몇시에 공항 가야 해요?"를 URL 인코딩하여 전달합니다.

## Upstage Solar 이전 계획

현재는 Google Gemini(Vertex AI) 기반으로 구현되어 있으며, **Upstage Solar Agent Partner Program**
선정 시 다음 방향으로 확장할 계획입니다.

1. **LLM 교체**: Vertex AI 호출부를 Upstage Solar API로 교체
    - 한국어 응답 품질 및 구조화된 데이터(JSON) 이해 강점을 활용
2. **에이전트화**: Solar Pro의 tool-use 기능을 활용해, 스케줄 조회를 LLM이
   직접 도구 호출로 수행하도록 확장 (현재는 애플리케이션이 먼저 데이터를 조회해 프롬프트에 주입하는 방식)
3. **평가**: 동일 시나리오에 대한 Gemini vs Solar 응답 품질/속도 비교

## About

VoiceCaddieX(APL Golf)에서 Upstage Solar Agent Partner Program 지원을 위해 작성한 프로젝트입니다.