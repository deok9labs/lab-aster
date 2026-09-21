# Swagger UI 사용법

## 구성

백엔드의 OpenAPI 명세와 Swagger UI는 `springdoc-openapi-starter-webmvc-ui`가 제공한다. 이 의존성은 HTTP 표현 계층의 관심사이므로 `adapter-in-web` module에만 둔다. `domain`과 `application`에서는 Springdoc 또는 Swagger annotation에 의존하지 않는다.

사용 버전은 Spring Boot 4.1 계열을 지원하는 안정 릴리스 `3.1.0`이다.

## 로컬 실행

OpenAPI endpoint는 별도 설정 없이 기본적으로 활성화된다.

PowerShell:

```powershell
./gradlew.bat :bootstrap:bootRun
```

macOS 및 Linux:

```shell
./gradlew :bootstrap:bootRun
```

실행 후 다음 주소를 사용한다.

| 용도 | 주소 |
| --- | --- |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |

## 운영 환경 주의사항

- 운영 환경에서는 `ASTER_OPENAPI_ENABLED=false`를 명시적으로 주입해 OpenAPI endpoint와 Swagger UI를 비활성화한다. 이 값이 누락되면 기본값 `true`가 적용되므로 배포 환경의 필수 설정으로 관리한다.
- 외부 공개가 필요하면 인증·인가, network 접근 제한 및 공개할 API 범위를 먼저 결정한다.
- Swagger UI만 숨기고 `/v3/api-docs`를 노출하거나 그 반대 상태를 만들지 않도록 두 기능을 같은 환경 변수로 제어한다.
- API 요청·응답 예시에 인증 정보, 개인정보 또는 내부 system 정보를 작성하지 않는다.

## 코드 문서와 OpenAPI 명세의 역할

- Javadoc은 구현의 역할, 호출 계약과 설계 이유를 개발자에게 설명한다.
- OpenAPI 명세는 HTTP 소비자에게 endpoint, parameter, request와 response schema를 제공한다.
- Java type과 Spring MVC mapping으로 계약이 명확하면 같은 설명을 Swagger annotation으로 반복하지 않는다.
- 자동 생성 결과만으로 제약이나 의미가 전달되지 않을 때만 inbound adapter의 HTTP 계약에 OpenAPI annotation을 추가한다.
- OpenAPI annotation을 `domain` 또는 `application`에 추가하지 않는다.

## 주간 일정 API 계약

주차는 임의의 시작일이 아니라 서버의 한국 시간 기준 상대 주차로 선택한다.

| 값 | 의미 |
| --- | --- |
| `current` | 서버 기준일이 포함된 월요일부터 일요일까지 |
| `next` | `current` 시작일에서 7일 뒤인 월요일부터 일요일까지 |

### 일정 조회

```http
GET /api/v1/schedules/{scheduleWeek}
```

응답 예시:

```json
{
  "weekStart": "2026-09-21",
  "weekEnd": "2026-09-27",
  "members": [
    {
      "id": 1,
      "name": "팀원",
      "server": "서버",
      "position": "MT",
      "submitted": true,
      "updatedAt": "2026-09-21T18:30:00+09:00"
    }
  ],
  "availability": [
    {
      "memberId": 1,
      "date": "2026-09-21",
      "ranges": [
        { "startTime": "18:00", "endTime": "19:00" },
        { "startTime": "21:30", "endTime": "24:00" }
      ]
    }
  ]
}
```

### 팀원 일정 전체 교체

```http
PUT /api/v1/schedules/{scheduleWeek}/members/{memberId}
Content-Type: application/json
```

요청 예시:

```json
{
  "expectedWeekStart": "2026-09-21",
  "ranges": [
    {
      "date": "2026-09-21",
      "startTime": "18:00",
      "endTime": "19:00"
    },
    {
      "date": "2026-09-21",
      "startTime": "21:30",
      "endTime": "24:00"
    }
  ]
}
```

- `expectedWeekStart`는 저장 대상을 지정하지 않고 화면을 연 뒤 주차가 바뀌었는지 검증한다.
- `ranges`는 교체 후 남길 전체 가용 범위이며 빈 배열은 해당 팀원의 선택을 모두 해제한다.
- 시작과 종료는 30분 단위이고 `18:00 <= startTime < endTime <= 24:00`이어야 한다.
- 한 범위의 날짜는 선택한 주의 월요일부터 일요일 사이여야 한다.
- 겹치거나 맞닿은 범위는 저장 과정에서 동일한 30분 슬롯으로 정규화된다.

응답 예시:

```json
{
  "memberId": 1,
  "weekStart": "2026-09-21",
  "revision": 2,
  "updatedAt": "2026-09-21T18:30:00+09:00"
}
```

### 오류 계약

오류 응답은 다음 공통 구조를 사용한다.

```json
{
  "code": "INVALID_SCHEDULE_REQUEST",
  "message": "일정 요청 값이 올바르지 않습니다."
}
```

| HTTP 상태 | 코드 | 조건 |
| --- | --- | --- |
| `400` | `INVALID_SCHEDULE_REQUEST` | 주차 값, 날짜, 시간 형식 또는 시간 범위가 올바르지 않음 |
| `404` | `MEMBER_NOT_FOUND` | 활성 상태인 팀원이 존재하지 않음 |
| `409` | `SCHEDULE_WEEK_MISMATCH` | `expectedWeekStart`와 서버가 계산한 주차가 다름 |
