# PostgreSQL 로컬 개발 환경

## 채택 버전

- 메이저 버전: PostgreSQL 18
- 개발 기준 마이너 버전: PostgreSQL 18.6
- 운영 원칙: 18.x 범위에서 보안 및 버그 수정이 포함된 최신 마이너 버전을 유지한다.

2026년 9월 15일 기준 PostgreSQL 18.6이 최신 안정 버전이며, PostgreSQL 18은 2030년 11월 14일까지 지원된다. PostgreSQL 프로젝트는 지원 중인 메이저 버전에서 최신 마이너 릴리스를 사용할 것을 권장한다.

- [PostgreSQL 버전 정책과 지원 현황](https://www.postgresql.org/support/versioning/)
- [PostgreSQL 18.6 릴리스 안내](https://www.postgresql.org/about/news/postgresql-186-1711-1615-1519-1424-and-19-beta-3-released-3365/)

PostgreSQL 19 Beta는 시험용 버전이므로 개발 및 운영 기준으로 사용하지 않는다.

## Windows 설치

PostgreSQL 공식 Windows 다운로드 페이지에서 안내하는 EDB 64비트 설치 프로그램을 사용한다.

1. [PostgreSQL Windows 다운로드](https://www.postgresql.org/download/windows/)에서 EDB 설치 프로그램으로 이동한다.
2. PostgreSQL 18의 최신 18.x 설치 프로그램을 내려받아 실행한다.
3. 구성 요소에서 PostgreSQL Server와 Command Line Tools를 선택한다. GUI 관리 도구가 필요하면 pgAdmin 4도 선택한다.
4. 데이터 디렉터리는 기본 경로를 사용하거나 로컬 개발 데이터 전용 경로를 지정한다.
5. `postgres` 관리자 계정의 비밀번호를 설정한다. 이 값을 프로젝트 파일, 명령 기록이나 문서에 작성하지 않는다.
6. 개발 포트는 다른 서비스와 충돌하지 않는 경우 기본값 `5432`를 사용한다.
7. Locale은 특별한 요구사항이 없으면 운영체제 기본값을 사용한다.
8. 설치를 완료한 뒤 새 터미널을 연다.

EDB 설치 프로그램에는 PostgreSQL Server, pgAdmin과 StackBuilder가 포함된다. 애플리케이션 실행에 StackBuilder는 필수가 아니다.

## 설치 확인

PowerShell에서 다음 명령을 실행한다.

```powershell
psql --version
```

`psql`을 찾지 못하면 PostgreSQL 설치 경로의 `bin` 디렉터리를 사용자 `PATH`에 추가한 뒤 터미널을 다시 연다.

서버 접속 여부는 비밀번호를 명령에 직접 포함하지 않고 대화형 입력으로 확인한다.

```powershell
psql -U postgres -h localhost -p 5432
```

접속 후 다음 쿼리로 서버 버전을 확인할 수 있다.

```sql
SELECT current_setting('server_version');
```

## 로컬 데이터베이스 준비

애플리케이션에서 관리자 계정을 직접 사용하지 않는다. `psql`에 관리자로 접속한 뒤 로컬 개발 전용 역할과 데이터베이스를 생성한다.

```sql
CREATE ROLE aster_app LOGIN;
\password aster_app
CREATE DATABASE aster OWNER aster_app;
```

`\password`가 표시하는 대화형 프롬프트에서 로컬 비밀번호를 입력한다. 실제 비밀번호는 Git 추적 파일에 저장하지 않는다.

## 애플리케이션 연결 원칙

백엔드 연결 설정을 추가할 때 다음 값을 환경 변수 또는 승인된 secret 관리 도구로 주입한다.

| 환경 변수 | 용도 | 로컬 기본 예시 |
| --- | --- | --- |
| `DB_HOST` | PostgreSQL 호스트 | `localhost` |
| `DB_PORT` | PostgreSQL 포트 | `5432` |
| `DB_NAME` | 애플리케이션 데이터베이스 | `aster` |
| `DB_USER` | 애플리케이션 전용 역할 | `aster_app` |
| `DB_PASSWORD` | 애플리케이션 역할 비밀번호 | 문서 및 저장소에 기록하지 않음 |

애플리케이션은 JPA와 PostgreSQL JDBC driver를 사용한다. Hibernate의 `ddl-auto`는 `validate`로 고정해 mapping 일치 여부만 확인하며 table을 생성하거나 변경하지 않는다. Schema와 기준 데이터는 운영자가 직접 관리하고 Flyway 같은 migration 도구는 사용하지 않는다.
