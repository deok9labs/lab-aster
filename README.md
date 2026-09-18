# lab-aster

deok9labs 서비스의 API와 backend 기능을 제공하는 Java 21·Spring Boot 기반 애플리케이션입니다.

## Database 연결

다음 환경 변수를 실행 환경 또는 승인된 secret 관리 도구로 주입합니다.

| 환경 변수 | 용도 | 기본값 |
| --- | --- | --- |
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | database 이름 | `POSTGRES_DB`, 없으면 `aster` |
| `DB_USER` | application 계정 | `POSTGRES_USER`, 없으면 `aster_app` |
| `DB_PASSWORD` | application 계정 비밀번호 | `POSTGRES_PASSWORD` |

비밀번호는 저장소에 기록하지 않습니다. 애플리케이션은 기존 schema의 mapping 일치 여부만 확인하며 schema를 생성하거나 변경하지 않습니다.

## 검증과 빌드

```powershell
./gradlew.bat clean build javadoc
```

실제 PostgreSQL 연결이 필요한 통합 테스트는 database 환경 변수가 설정된 실행 환경에서 동작합니다.

## Container 배포

Gradle build가 생성한 `bootstrap/build/libs/aster.jar`를 repository root의 `Dockerfile`로 image화합니다. 배포 환경에서는 database 자격 증명을 container 환경 변수로만 주입합니다.

Nginx만 외부 HTTP port를 공개하며 애플리케이션과 PostgreSQL port는 외부에 직접 공개하지 않습니다. 운영 OpenAPI와 Swagger UI는 비활성화합니다.

전체 구조와 계층 간 객체 전달 규칙은 [아키텍처 문서](doc/architecture.md)를 따릅니다.
