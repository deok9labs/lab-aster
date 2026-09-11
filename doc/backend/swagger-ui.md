# Swagger UI 사용법

## 구성

백엔드의 OpenAPI 명세와 Swagger UI는 `springdoc-openapi-starter-webmvc-ui`가 제공한다. 이 의존성은 HTTP 표현 계층의 관심사이므로 `adapter-in-web` module에만 둔다. `domain`과 `application`에서는 Springdoc 또는 Swagger annotation에 의존하지 않는다.

사용 버전은 Spring Boot 4.1 계열을 지원하는 안정 릴리스 `3.1.0`이다.

## 로컬 실행

OpenAPI endpoint는 기본적으로 비활성화되어 있다. 로컬에서 사용할 때 `ASTER_OPENAPI_ENABLED` 환경 변수를 `true`로 설정하고 backend를 실행한다.

PowerShell:

```powershell
$env:ASTER_OPENAPI_ENABLED = "true"
./gradlew.bat :bootstrap:bootRun
```

macOS 및 Linux:

```shell
ASTER_OPENAPI_ENABLED=true ./gradlew :bootstrap:bootRun
```

실행 후 다음 주소를 사용한다.

| 용도 | 주소 |
| --- | --- |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| OpenAPI YAML | `http://localhost:8080/v3/api-docs.yaml` |

PowerShell에서 실행을 마친 뒤 현재 session의 환경 변수를 제거하려면 다음 명령을 사용한다.

```powershell
Remove-Item Env:ASTER_OPENAPI_ENABLED
```

## 운영 환경 주의사항

- 운영 환경에서는 `ASTER_OPENAPI_ENABLED`의 기본값인 `false`를 유지한다.
- 외부 공개가 필요하면 인증·인가, network 접근 제한 및 공개할 API 범위를 먼저 결정한다.
- Swagger UI만 숨기고 `/v3/api-docs`를 노출하거나 그 반대 상태를 만들지 않도록 두 기능을 같은 환경 변수로 제어한다.
- API 요청·응답 예시에 인증 정보, 개인정보 또는 내부 system 정보를 작성하지 않는다.

## 코드 문서와 OpenAPI 명세의 역할

- Javadoc은 구현의 역할, 호출 계약과 설계 이유를 개발자에게 설명한다.
- OpenAPI 명세는 HTTP 소비자에게 endpoint, parameter, request와 response schema를 제공한다.
- Java type과 Spring MVC mapping으로 계약이 명확하면 같은 설명을 Swagger annotation으로 반복하지 않는다.
- 자동 생성 결과만으로 제약이나 의미가 전달되지 않을 때만 inbound adapter의 HTTP 계약에 OpenAPI annotation을 추가한다.
- OpenAPI annotation을 `domain` 또는 `application`에 추가하지 않는다.
