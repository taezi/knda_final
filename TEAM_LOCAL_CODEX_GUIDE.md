# 팀원용 로컬 실행 가이드 (Windows)

이 안내서는 팀원이 자기 PC에서 LGDashboard를 실행하고, 자기 계정으로 로그인한 Codex CLI를 최종 AX 권고 기능에 연결하는 방법입니다.

> Codex CLI는 **백엔드가 실행 중인 PC**에서 실행됩니다. 따라서 이 방법에서는 각 팀원이 자신의 PC에서 프론트엔드와 백엔드를 모두 실행해야 합니다. 배포된 Render 서버와는 연결되지 않습니다.

## 1. 준비물

- Windows 10/11
- JDK 21
- Node.js 20.19 이상 또는 22.12 이상과 npm (프로젝트 Vite 요구 버전)
- Codex CLI를 사용할 수 있는 본인 OpenAI/ChatGPT 계정
- 전달받은 LGDashboard 프로젝트 소스

## 2. 프로젝트 폴더 준비

Git을 쓰지 않는 경우 프로젝트 폴더를 ZIP으로 받아 원하는 위치에 압축 해제합니다. 예를 들어 다음처럼 둘 수 있습니다.

```text
C:\knda\final_project
├─ backend
└─ frontend
```

전달할 때는 `backend/.env`를 ZIP에 넣지 마세요. 개인 인증정보와 PC별 경로가 포함될 수 있으므로 각자 자기 PC에서 만듭니다. `frontend/node_modules`, `frontend/dist`, `backend/build`는 전달하지 않아도 됩니다.

## 3. Codex CLI 설치 및 본인 계정 로그인

PowerShell 또는 Windows Terminal에서 실행합니다.

```powershell
npm install -g @openai/codex@latest
codex --version
codex
```

처음 `codex`를 실행하면 로그인 절차가 표시됩니다. 본인 계정으로 로그인하고, 대화형 화면이 열리면 `exit` 또는 `Ctrl+C`로 종료합니다. 다른 팀원의 계정, 비밀번호, Codex 인증 파일을 공유하거나 복사하지 마세요.

CLI 설치와 로그인이 정상인지 확인하려면 아래 명령으로 Codex의 비대화형 실행 옵션이 표시되는지 확인합니다.

```powershell
codex exec --help
```

Codex CLI 설치·로그인 과정은 [OpenAI Codex CLI 안내](https://developers.openai.com/codex/cli/)를 참고하세요.

## 4. Codex 및 Node.js 경로 확인

아래 명령으로 본인 PC의 실행 파일 경로를 확인합니다.

```powershell
where.exe codex
where.exe node
```

Codex 경로 중 `codex.cmd`로 끝나는 경로와 Node.js 폴더 경로를 메모합니다. 보통 Codex는 `%APPDATA%\npm\codex.cmd`, Node.js는 `C:\Program Files\nodejs` 아래에 있습니다. PC마다 다를 수 있으므로 실제 출력 경로를 사용하세요.

## 5. 백엔드 `.env` 만들기

`backend` 폴더 안에 `.env`라는 파일을 만들고 다음 항목을 입력합니다. 아래 경로는 예시이므로 **본인 PC에서 확인한 경로와 프로젝트 경로로 바꿔야 합니다.**

```properties
CODEX_CLI_ENABLED=true
CODEX_CLI_COMMAND=C:/Users/본인Windows계정/AppData/Roaming/npm/codex.cmd
CODEX_NODE_DIRECTORY=C:/Program Files/nodejs
CODEX_CLI_WORKING_DIRECTORY=C:/knda/final_project/backend
CODEX_CLI_TIMEOUT_SECONDS=90
```

중요:

- Windows 경로 구분자는 역슬래시(`\`) 대신 슬래시(`/`)를 사용합니다. 이 프로젝트는 `.env`를 Spring의 properties 형식으로 읽어 역슬래시가 사라질 수 있습니다.
- 값에 따옴표를 덧붙이지 마세요.
- `CODEX_CLI_WORKING_DIRECTORY`는 본인 PC에서 `backend` 폴더의 실제 경로로 설정합니다.
- `.env`는 Git에 올라가지 않도록 제외되어 있습니다. 그래도 화면 공유나 메신저에 인증정보를 올리지 마세요.

네이버 뉴스 기능도 사용할 팀원은 본인이 발급받은 네이버 API 키를 별도로 설정해야 합니다. 키가 없다면 그 기능의 뉴스 검색은 사용할 수 없을 수 있지만, Codex 최종 권고 설정과는 별개입니다.

## 6. 백엔드 실행

### IntelliJ 사용

1. IntelliJ에서 `backend` 폴더를 프로젝트로 엽니다.
2. `BackendApplication` 실행 구성을 선택합니다.
3. 실행 구성의 Working directory가 본인 PC의 `.../final_project/backend`인지 확인합니다. `.env`를 이 위치에서 읽습니다.
4. 백엔드를 실행하고 콘솔에 `Tomcat started on port 8080`이 표시되는지 확인합니다.

### 터미널 사용

PowerShell에서 `backend` 폴더로 이동한 뒤 실행합니다.

```powershell
cd C:\knda\final_project\backend
.\gradlew.bat bootRun
```

## 7. 프론트엔드 실행

별도의 PowerShell 창을 열고 아래를 실행합니다.

```powershell
cd C:\knda\final_project\frontend
npm install
npm run dev
```

터미널에 표시된 로컬 주소(기본값 `http://localhost:5173`)를 같은 PC의 브라우저에서 엽니다. 현재 프론트엔드는 백엔드를 `http://localhost:8080`에서 찾으므로 두 프로그램이 모두 실행 중이어야 합니다.

## 8. 최종 AX 권고 기능 확인

1. 대시보드에서 필요한 도구를 실행해 통합 분석 결과를 준비합니다.
2. **최종 AX 권고 생성**을 누릅니다.
3. 실제 Codex 응답이면 권고 내용이 표시됩니다.
4. 실패하면 IntelliJ 백엔드 콘솔의 `[Codex CLI]` 오류를 확인합니다.

## 자주 발생하는 문제

| 증상 | 확인할 내용 |
| --- | --- |
| `Codex CLI 실행 파일을 찾지 못했습니다` | `where.exe codex` 결과의 `codex.cmd` 경로를 `CODEX_CLI_COMMAND`에 슬래시(`/`)로 입력했는지 확인 |
| 사용자 홈 경로·로그인 오류 | 본인 계정으로 `codex`를 직접 실행해 로그인 절차가 끝났는지 확인 |
| `.env` 값을 읽지 못함 | 파일 위치가 `backend/.env`인지, IntelliJ Working directory가 `backend`인지 확인 |
| 포트 8080 사용 중 | 다른 백엔드가 이미 실행 중인지 확인하고 하나만 실행 |
| Codex 사용량/접근 오류 | 본인 계정의 Codex 접근 권한 및 이용 가능 상태 확인 |

각 팀원은 자기 Codex 계정과 사용 가능 범위로 요청을 처리합니다. 팀원의 로그인 상태나 인증정보는 서로 공유되지 않습니다.

## 공식 참고

- [OpenAI Codex CLI 설치 및 시작 안내](https://developers.openai.com/codex/cli/)
- [Codex CLI 예시와 npm 설치 명령](https://developers.openai.com/cookbook/examples/codex/using_goals_in_codex)
