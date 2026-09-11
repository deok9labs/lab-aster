# 코드 주석 작성 가이드

## 목적

주석은 코드가 표현하지 못하는 의도, 계약, 제약과 결정 이유를 다음 작업자에게 전달한다. 읽는 대상에는 사용자와 에이전트를 모두 포함한다. 주석의 양보다 정확성, 지속 가능성과 정보 가치를 우선한다.

## 필수 작성 대상

- 외부에서 사용하는 공개 API와 module·layer 경계
- business rule, invariant 및 허용하지 않는 상태
- 보안, 권한, 개인정보 처리와 관련된 결정
- transaction, 동시성, 재시도, 멱등성과 비동기 처리의 제약
- 외부 API, database, message broker 및 browser의 제약에 대응하는 구현
- 직관적이지 않은 algorithm, 성능 최적화, 예외 처리 및 우회 구현
- 제거 시점이나 조건이 있는 임시 구현

단순 대입, 언어 관례에 따른 boilerplate, 이름만으로 의도가 명확한 코드에는 설명을 반복하는 주석을 작성하지 않는다.

## 작성 원칙

1. 무엇을 하는지보다 왜 이 구현과 제약이 필요한지 설명한다.
2. 호출자가 지켜야 할 전제 조건과 보장되는 결과를 명확히 한다.
3. 구현 세부보다 오래 유지되는 계약과 business 의미를 기록한다.
4. 확인되지 않은 추측은 사실처럼 기록하지 않는다.
5. 관련 코드가 변경되면 주석도 같은 변경에서 검토하고 갱신한다.

## Java Javadoc 예시

### inbound port

```java
/**
 * 사용자가 요청한 인사말을 생성하는 유스케이스 경계다.
 *
 * <p>생성된 인사말은 반환 전에 감사 로그 대상으로 전달된다.</p>
 */
public interface GreetingUseCase {

    /**
     * 표시 가능한 인사말을 생성한다.
     *
     * @param name 앞뒤 공백을 제거한 뒤 비어 있지 않아야 하는 사용자 이름
     * @return 감사 로그 전달이 완료된 인사말
     * @throws IllegalArgumentException 이름이 비어 있을 때
     */
    String greet(String name);
}
```

### domain invariant

```java
/**
 * 사용자에게 표시할 인사말이다.
 *
 * <p>빈 문구는 유효한 인사말이 아니므로 생성 시점에 거부한다.</p>
 */
public record Greeting(String message) {
    public Greeting {
        if (message.isBlank()) {
            throw new IllegalArgumentException("인사말은 비어 있을 수 없습니다.");
        }
    }
}
```

`@param`, `@return`, `@throws`는 이름과 type 이상의 계약을 전달하지 못한다면 생략한다. 상위 interface의 계약을 그대로 구현하는 method는 Javadoc을 복제하지 않는다.

## TSDoc 예시

```ts
/**
 * 검색어 변경을 지연 반영해 불필요한 API 요청을 줄인다.
 *
 * @param query 사용자가 입력한 원본 검색어
 * @param delayMs 마지막 입력 이후 반영을 기다릴 시간
 * @returns 지연 시간이 지난 뒤 확정된 검색어
 */
export function useDebouncedQuery(query: string, delayMs: number): string {
  // 구현 생략
}
```

props의 type이나 함수 이름으로 의미가 충분하면 설명을 반복하지 않는다. side effect의 발생 시점, cleanup 조건 또는 비동기 응답의 우선순위처럼 호출자가 알아야 하는 계약을 우선 기록한다.

## 내부 주석 예시

좋은 주석은 선택의 이유와 유지 조건을 설명한다.

```java
// 외부 결제사의 중복 요청 가능성 때문에 승인 결과를 저장한 뒤 응답한다.
paymentResultRepository.save(result);
```

다음처럼 코드를 그대로 읽어 주는 주석은 작성하지 않는다.

```java
// 승인 결과를 저장한다.
paymentResultRepository.save(result);
```

## TODO와 FIXME

임시 작업에는 추적 가능한 issue와 제거 조건을 함께 적는다.

```text
TODO(ASTER-142): 신규 인증 API 전환이 완료되면 호환 변환기를 제거한다.
```

issue 식별자나 완료 조건이 없는 `TODO`, 담당자 이름만 적은 메모, 기한 없이 “나중에 개선”이라고 적은 주석은 허용하지 않는다.

## 아키텍처 결정 기록

여러 파일이나 module에 영향을 주는 결정은 코드 한 지점의 주석만으로 설명하지 않는다. project 전체 사용법은 README에, 선택 배경·대안·결과를 보존해야 하는 결정은 ADR에 기록하고 관련 코드에서는 해당 문서를 참조한다.

## 완료 점검표

- 공개 API와 경계의 역할 및 계약이 문서화되었는가?
- 코드만으로 알기 어려운 business rule과 기술적 제약의 이유가 설명되었는가?
- 주석이 코드를 반복하거나 구현 세부에 과도하게 결합되어 있지 않은가?
- 변경된 코드와 모순되거나 더 이상 유효하지 않은 주석이 없는가?
- `TODO`와 `FIXME`에 issue 식별자와 완료 또는 제거 조건이 있는가?
- module 간 결정이 필요한 경우 project 문서나 ADR에도 기록되었는가?
