# 기술 스택

## 핵심 환경

| 구분 | 기술 | 기준 |
| --- | --- | --- |
| Language | Java | 21 LTS |
| Development JDK | Eclipse Temurin | Java 21 LTS 필수 |
| Java Toolchain | Gradle Toolchain | Eclipse Temurin 21 |
| Framework | Spring Boot | 4.1.1 |
| API | REST API | JSON over HTTP, inbound adapter로 구현 |
| Build | Gradle | Kotlin DSL 및 Gradle Wrapper |
| Test | JUnit | JUnit 5 |
| Logging | Log4j2 | Spring Boot Log4j2 starter |
| Architecture | Hexagonal Architecture | Ports and Adapters |

## 모듈 구조

| 모듈 | 역할 |
| --- | --- |
| `domain` | business rule과 invariant |
| `application` | inbound·outbound port와 유스케이스 구현 |
| `adapter-in-web` | REST API inbound adapter |
| `bootstrap` | Spring Boot 실행과 port·adapter 조립 |

의존성은 adapter와 `bootstrap`에서 `application`과 `domain` 방향으로만 향합니다. outbound adapter 모듈은 실제 외부 시스템 경계가 생길 때 추가합니다.

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
