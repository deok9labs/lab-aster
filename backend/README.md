# 기술 스택

## 핵심 환경

| 구분 | 기술 | 기준 |
| --- | --- | --- |
| Language | Java | 21 LTS |
| Development JDK | Java 21 호환 배포판 | Eclipse Temurin 권장 |
| Java Toolchain | Gradle Toolchain | Java 21 |
| Framework | Spring Boot | 4.1.1 |
| Build | Gradle | Kotlin DSL 및 Gradle Wrapper |
| Architecture | Hexagonal Architecture | Ports and Adapters |

## 임시 기본 구조

헥사고날 아키텍처의 의존성 방향을 검증하기 위해 Hello World 유스케이스를 멀티 모듈 기본 구조로 제공합니다. `Temp`로 시작하는 Java 타입은 실제 기능 구현을 시작할 때 검토 후 삭제합니다.

```text
GET /hello
  → inbound adapter
  → inbound port
  → application service
  → outbound port
  → logging adapter
```

## 실행 및 검증

Windows:

```shell
.\gradlew.bat :bootstrap:bootRun
.\gradlew.bat test
```

macOS 및 Linux:

```shell
./gradlew :bootstrap:bootRun
./gradlew test
```

Swagger UI의 활성화 방법과 운영 환경 주의사항은 [`../doc/backend/swagger-ui.md`](../doc/backend/swagger-ui.md)를 참고합니다.
