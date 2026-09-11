> [!IMPORTANT]
> **이 디렉토리와 하위 경로의 작업은 루트 `AGENTS.md` 규칙을 상속한다. 이 문서는 백엔드 작업에 적용할 추가 규칙이며, 규칙이 충돌하면 루트 `AGENTS.md`를 우선한다.**

## 아키텍처 규칙

- 백엔드 코드는 헥사고날 아키텍처(Ports and Adapters)에 따라 구현한다.
- 의존성은 adapter에서 application과 domain을 향하도록 구성한다.
- domain과 application은 adapter 구현에 의존하지 않는다.
- 외부 요청은 inbound adapter가 inbound port를 통해 application에 전달한다.
- 데이터베이스, 외부 API 및 메시징 등 외부 시스템과의 연결은 outbound port와 outbound adapter를 통해 구성한다.
- 실제 유스케이스나 외부 시스템 경계가 없는 경우에는 불필요한 port와 adapter를 미리 만들지 않는다.
