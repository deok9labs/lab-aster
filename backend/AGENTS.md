> [!IMPORTANT]
> **이 디렉토리와 하위 경로의 작업은 루트 `AGENTS.md` 규칙을 상속한다. 이 문서는 백엔드 작업에 적용할 추가 규칙이며, 규칙이 충돌하면 루트 `AGENTS.md`를 우선한다.**

## 아키텍처 규칙

- 백엔드 코드는 헥사고날 아키텍처(Ports and Adapters)에 따라 구현한다.
- 의존성은 adapter에서 application과 domain을 향하도록 구성한다.
- domain과 application은 adapter 구현에 의존하지 않는다.
- 외부 요청은 inbound adapter가 inbound port를 통해 application에 전달한다.
- 데이터베이스, 외부 API 및 메시징 등 외부 시스템과의 연결은 outbound port와 outbound adapter를 통해 구성한다.
- 실제 유스케이스나 외부 시스템 경계가 없는 경우에는 불필요한 port와 adapter를 미리 만들지 않는다.

---

## Java 문서화 규칙

- 공개 class, interface, record 및 enum에는 역할과 책임을 설명하는 Javadoc을 작성한다.
- 공개 또는 보호 method에는 호출 계약, 입력 조건, 반환 의미와 발생 가능한 예외 중 코드만으로 명확하지 않은 내용을 Javadoc으로 작성한다.
- `@param`, `@return`, `@throws`는 의미 있는 정보를 제공할 때만 사용하며 이름이나 type을 반복하지 않는다.
- 상위 계약을 변경 없이 구현하는 단순 override는 문서를 중복하지 않고 상위 Javadoc을 상속할 수 있다.
- domain에는 핵심 business rule과 invariant를, inbound port에는 use case의 목적·입력·결과·실패 조건을 문서화한다.
- outbound port에는 application이 기대하는 외부 기능의 계약과 실패 의미를 문서화한다.
- adapter에는 외부 시스템 제약, 변환 규칙과 오류 처리 이유 중 구현만으로 드러나지 않는 내용을 문서화한다.
- application service의 내부 주석은 처리 순서를 반복하지 않고 business 결정과 transaction 경계의 이유를 설명한다.
- Java와 헥사고날 아키텍처의 구체적인 예시는 `../doc/code-comment-guide.md`를 따른다.
