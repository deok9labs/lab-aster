# 기술 스택

## 핵심 환경

| 구분 | 기술 | 기준 |
| --- | --- | --- |
| Language | Java | 21 LTS |
| Development JDK | Eclipse Temurin | Java 21 LTS 필수 |
| Java Toolchain | Gradle Toolchain | Eclipse Temurin 21 |
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

빌드와 실행에는 Eclipse Temurin 21이 필요합니다. Gradle Toolchain이 Java 버전과 vendor를 검사하므로 다른 JDK 배포판이나 버전을 사용하면 빌드가 중단됩니다. `JAVA_HOME`과 `PATH`를 Temurin 21 설치 경로로 설정한 후 다음 명령으로 현재 환경을 확인합니다.

```shell
java -version
```

출력에서 Java 버전 `21`과 `Temurin`을 확인한 후 Gradle Wrapper를 사용합니다.

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
