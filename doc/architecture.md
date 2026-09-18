# Aster 아키텍처

## 범위

Aster는 여러 프런트엔드의 업무 API를 함께 수용할 수 있는 계층형 헥사고날 애플리케이션이다. 현재 `ember` 영역은 주간 일정 조회와 팀원별 일정 교체를 제공한다.

## 의존 방향

```text
ember.adapter.in.web -> ember.application -> ember.domain
ember.adapter.out.persistence -> ember.application -> ember.domain
ember.configuration -> adapters + application
```

Domain은 외부 기술에 의존하지 않는다. Application은 유스케이스와 외부 시스템 port를 정의하지만 HTTP, Spring MVC, JPA와 PostgreSQL 타입을 사용하지 않는다. Adapter는 경계에서 자신의 전용 객체를 Application 계약으로 명시적으로 변환한다.

## 계층 간 데이터 계약

- Web request/response DTO는 `adapter-in-web` 밖으로 전달하지 않는다.
- JPA entity와 repository는 `adapter-out-persistence` 밖으로 전달하지 않는다.
- Inbound adapter는 request DTO를 Application command로, Application result를 response DTO로 변환한다.
- Outbound adapter는 Application의 persistence command/data와 JPA entity를 명시적으로 변환한다.
- 의미가 불명확한 `Map<String, Object>`나 계층 공용 DTO를 경계 계약으로 사용하지 않는다.

## 데이터베이스와 트랜잭션

데이터베이스 스키마와 기준 데이터는 운영자가 직접 관리한다. Hibernate는 `validate`만 수행하며 schema를 생성하거나 변경하지 않는다. Flyway migration은 사용하지 않는다.

팀원 일정 교체는 기존 slot 삭제, 새 slot 저장, submission revision과 수정 시각 갱신을 하나의 persistence adapter 트랜잭션에서 처리한다. 상세 변경 이력은 보존하지 않으며 현재 slot은 hard delete한다.

## 현재 제약과 후속 범위

- API에는 인증과 권한 검사가 없으며 활성 팀원의 일정은 누구나 수정할 수 있다.
- 한국 시간 기준 현재 주만 조회하고 수정한다.
- 과거·미래 주 조회와 수정은 지원하지 않는다.
- revision은 기록하지만 동시 수정 충돌을 거부하지 않으며 마지막으로 완료된 저장이 최종 상태가 된다.
