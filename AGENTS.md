> [!IMPORTANT]
> **아래 규칙을 기본 원칙으로 준수하며, 상위 지시 또는 이 문서에 명시된 예외가 있으면 해당 지시를 우선한다. 같은 수준의 규칙이 충돌하면 먼저 제시된 규칙을 우선한다.**

## 민감 정보 보호 규칙

### 보호 대상

- 인증 및 암호화 정보: 비밀번호, API key, token, private key, 인증서 비밀값
- 개인정보 및 고객 데이터: 개인 식별정보, 연락처, 금융·결제 정보, 고객 데이터
- 시스템 접근 정보: 데이터베이스 접속 정보, 내부 주소, cloud 자격 증명, 운영 설정
- `.env`, keystore, database dump, backup, 로그는 민감 정보가 포함될 가능성이 높은 검사 대상으로 취급한다.
- 보호 대상 목록은 예시이며, 공개 여부가 불분명하거나 공개 시 접근·식별·추적 또는 피해에 이용될 수 있는 정보는 **민감 정보로 취급한다**.

### 저장 및 출력

- 민감 정보는 코드, 설정, 로그, test fixture에 **직접 작성하지 않는다**.
- 민감 정보는 환경 변수 또는 승인된 **secret 관리 도구**로 주입한다.
- 민감 정보의 **전체 값을 응답이나 로그에 출력하지 않는다**.
- 보고가 필요하면 **값을 마스킹**하고 위치와 정보 종류만 알린다.

### Git 반영

- `commit` 전에는 staged 변경과 새로 추적할 파일에서 민감 정보를 확인한다.
- `push` 전에는 원격에 새로 전송될 전체 `commit`과 파일에서 민감 정보를 확인한다.
- 민감 정보 검사는 값 자체를 응답이나 로그에 출력하지 않는 방식으로 수행한다.
- 민감 정보가 포함된 파일이나 변경은 **`commit`하지 않는다**.
- 원격에 새로 전송될 Git 이력에서 민감 정보가 발견되면 **`push`를 중단한다**.

### 노출 대응

- 민감 정보가 노출되면 값을 재출력하지 않고 **사용자에게 즉시 알린다**.
- 파일에서 **삭제하는 것만으로 문제가 해결된 것으로 판단하지 않는다**.
- 인증 정보의 폐기·재발급과 Git 이력 수정은 제안한 후 **사용자의 명시적인 지시에 따라 진행한다**.

---

## 작업 수행 규칙

- 사용자가 직접 지시한 범위의 파일 추가 및 수정은 승인된 것으로 간주한다.
- 지시 범위를 벗어나거나 결과를 크게 바꿀 수 있는 변경은 먼저 제안하고, **사용자의 명시적인 지시를 받은 후** 진행한다.
- 기존 동작과 관련 없는 사용자 변경은 **수정하거나 되돌리지 않는다**.

---

## Git 작업 승인 규칙

- branch 생성, `commit`, `push`, Pull Request 생성, merge 및 branch 삭제는 각각 제안할 수 있지만, 사용자가 명시적으로 지시한 작업만 진행한다.
- 하나의 Git 작업에 대한 지시를 다른 Git 작업의 승인으로 확대 해석하지 않는다.
- 사용자가 앞서 제안된 여러 Git 작업을 명확히 지칭해 모두 진행하도록 지시하면 해당 작업은 각각 승인된 것으로 간주한다.

---

## 위험 작업 규칙

- `force push`, commit 이력 재작성, `reset --hard`, 추적 파일 정리 및 tag 삭제는 위험과 영향을 먼저 설명하고 사용자의 명시적인 지시를 받은 후 진행한다.
- `--no-verify` 또는 동등한 방법으로 hook이나 검증을 우회하지 않는다.

---

## 검증 규칙

- 변경 영향과 프로젝트에서 제공하는 명령에 따라 필요한 `test`, `lint`, `build`를 선별해 실행한다.
- 검증에 실패하면 원인을 확인하고 실패 사실을 숨기지 않는다.
- 실행하지 못한 검증과 남아 있는 위험 요소는 완료 보고에 명시한다.

---

## 코드 문서화 규칙

- 코드의 추가 및 수정 시 사용자와 에이전트가 역할, 의도, 계약 및 제약을 이해할 수 있도록 필요한 주석을 함께 작성한다.
- 공개 API와 module·layer 경계에는 해당 언어의 표준 문서 주석 형식을 사용한다.
- business rule, 보안, 동시성, transaction, 외부 시스템 제약, 예외 처리 및 우회 구현처럼 코드만으로 이유를 알기 어려운 결정은 주석으로 설명한다.
- 주석은 코드가 수행하는 동작을 반복하지 않고, 코드만으로 드러나지 않는 이유와 계약을 설명한다.
- 코드 변경으로 사실과 달라진 주석은 같은 변경에서 수정하거나 제거한다. 오래되었거나 추측에 근거한 주석을 남기지 않는다.
- `TODO`와 `FIXME`에는 추적 가능한 issue 식별자와 완료 또는 제거 조건을 기록한다. 식별자 없이 막연한 작업을 남기지 않는다.
- 여러 파일이나 module에 걸친 아키텍처 결정은 코드 주석만으로 관리하지 않고 project 문서 또는 ADR에 기록한다.
- 구체적인 작성 형식과 검토 기준은 `doc/code-comment-guide.md`를 따른다.

---

## Git 및 GitHub 설정

### Git 사용자 설정

- `commit` 전에 로컬 저장소의 사용자 이름과 이메일 설정 여부를 확인한다.
- 사용자 정보가 설정되어 있지 않으면 값을 요청하고, **사용자의 명시적인 지시를 받은 후** 로컬 Git 설정에만 저장한다.
- 사용자 이름과 이메일은 프로젝트 파일에 기록하지 않고 응답이나 로그에 불필요하게 재출력하지 않는다.
- Git의 `user.email`에는 GitHub 계정의 `Emails` 페이지에 표시된 GitHub 제공 `noreply` 이메일 주소만 **사용한다**.
- GitHub에서 제공한 `noreply` 이메일 이외의 주소로 `commit`을 생성하거나 `push`하지 않는다.

### GitHub 이메일 보호 설정

- `Keep my email addresses private`와 `Block command line pushes that expose my email` 설정은 임의로 변경하지 않는다.
- 설정 상태를 직접 확인할 수 없으면 사용자가 제공한 정보를 신뢰한다.

---

## branch 규칙

- `main`은 검증이 완료된 변경만 유지하는 안정 branch로 사용한다.
- `develop`은 다음 `main` 반영 대상 변경을 통합하고 검증하는 장기 branch로 사용한다.
- `main`과 `develop`에서는 작업 파일을 직접 수정하거나 `commit`하지 않는다.
- 작업 branch를 생성하기 전에 미커밋 변경이 있는지 확인하고, 기존 변경이 있으면 임의로 이동하거나 포함하지 않는다.
- 원격 변경을 동기화하기 위한 fast-forward 방식의 update는 허용한다.
- 원격 저장소가 있으면 `main`을 fast-forward 방식으로 동기화하고, `develop`은 최초 생성 시 최신 `main`에서 생성한다.
- `develop`에 없는 변경이 `main`에 반영되면 새로운 작업 branch를 생성하기 전에 해당 변경을 `develop`에 동기화한다.
- 원격 저장소가 있으면 작업 branch의 기준 branch를 fast-forward 방식으로 동기화한 후 작업 branch를 생성한다.
- 동기화할 수 없으면 작업을 중단하고 사용자에게 알린다.
- 긴급 수정 여부는 사용자가 명시적으로 결정한다. 긴급 수정이 필요할 가능성이 있으면 이유와 영향을 설명하고 사용자에게 긴급 수정 절차 적용 여부를 확인한다.
- 사용자가 긴급 수정을 지시하면 최신 `main`에서 `fix` branch를 생성하고 Pull Request로 반영한다.
- 긴급 수정을 제외한 작업 branch는 하나의 논리적인 변경 목적마다 최신 `develop`에서 생성한다.
- 같은 목적의 여러 `commit`은 하나의 branch에 포함할 수 있지만, 서로 독립적인 변경은 별도의 branch로 분리한다.
- 최초 `commit`처럼 작업 branch를 만들 수 없는 경우에는 이유를 설명하고, **사용자의 명시적인 지시를 받은 후** `main`에서 진행한다.
- branch 이름은 `type/kebab-case-summary` 형식으로 작성한다.
- `type`은 `feat`, `fix`, `docs`, `refactor`, `test`, `chore` 중에서 선택한다.

---

## commit 규칙

- 하나의 `commit`에는 독립적으로 설명할 수 있는 하나의 논리적 변경만 포함한다.
- 구현과 직접 관련된 `test` 및 문서는 같은 논리적 변경에 포함할 수 있다.
- 서로 독립적인 변경은 별도의 `commit`으로 나눈다.

### commit 메시지 규칙

- `commit` 메시지는 `type: summary` 형식으로 작성한다.
- `type`은 변경 성격에 따라 `feat`, `fix`, `docs`, `refactor`, `test`, `chore` 중에서 선택한다.
- `summary`는 **한글로 작성**하고, 변경 내용을 명확하고 간결하게 표현하며 마침표를 붙이지 않는다.
- `commit` 본문은 필요하면 변경 이유와 영향을 설명한다.

---

## Pull Request 및 merge 규칙

- 모든 Pull Request는 merge 전에 merge 가능한 상태이고 필수 status check가 모두 통과했는지 확인한다.
- 충돌이나 실패한 check가 있으면 merge하지 않고 사용자에게 알린다.
- 긴급 수정 branch를 제외한 작업 branch는 변경 검증을 완료한 후 Pull Request를 통해 `develop`에 merge한다.
- 작업 branch가 merge된 `develop`에서는 변경 간의 충돌과 통합 동작을 검증한다.
- `develop`에서 `main`으로의 Pull Request는 사용자가 반영 범위를 확정하고, 해당 범위의 변경이 모두 `develop`에 통합되었으며, `main`과 `develop`의 차이가 확정된 범위와 일치할 때 생성한다.
- Pull Request 생성 후 `develop`이 변경되면 현재 Pull Request의 변경 범위와 통합 검증 결과를 다시 확인한다.
- `develop`에서 `main`으로 merge할 때는 현재 Pull Request에 포함된 변경이 사용자가 확정한 범위와 일치하고, 해당 변경을 기준으로 필요한 검증이 완료되었는지 확인한다.
- `develop`에서 `main`으로의 Pull Request는 두 branch의 계보를 유지하기 위해 `merge commit` 방식으로 merge한다.
- `develop`에서 `main`으로 merge한 직후 두 branch의 파일 내용이 동일하고 기존 `develop`이 최신 `main`의 조상인지 확인한다.
- 위 조건을 만족하면 다음 작업 branch를 만들기 전에 `develop`을 최신 `main`으로 fast-forward한다. branch protection으로 직접 update할 수 없으면 `main`에서 `develop`으로 Pull Request를 생성해 동기화한다.
- 작업 branch에서 `develop`으로의 Pull Request는 branch의 원래 commit과 통합 이력을 보존하기 위해 `merge commit` 방식으로 merge한다.
- 긴급 수정 branch에서 `main`으로의 Pull Request는 branch의 원래 commit과 통합 이력을 보존하기 위해 `merge commit` 방식으로 merge한다.
- 긴급 수정이 `main`에 merge되면 `main`에서 `develop`으로 Pull Request를 생성하고 `merge commit` 방식으로 반영한다.
- 다른 merge 방식이 필요하면 이유와 영향을 설명하고 사용자의 명시적인 지시를 받는다.
- 작업 branch가 대상 branch에 merge되면 해당 작업 branch의 로컬과 원격 삭제를 사용자에게 제안한다.
- 작업 branch를 삭제하기 전에 해당 Pull Request가 대상 branch에 merge되었는지 확인한다.
- `rebase` 또는 `squash` merge로 일반 삭제가 불가능하면 merge 상태를 확인한 후 강제 삭제를 제안한다.
- `main`과 `develop`은 작업 branch 삭제 대상에 포함하지 않는다.
