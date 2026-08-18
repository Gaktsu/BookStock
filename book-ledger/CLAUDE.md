# 서재 원장 — 중고책 관리 자동화 도구

## 프로젝트 개요
집에 있는 책을 중고로 팔기 위한 개인용 로컬 도구.
- ISBN 스캔/입력 → 알라딘 API로 도서 정보·시세 조회
- 이미 등록된 책이면 중복 알림 (재등록 방지)
- 여러 중고 마켓(알라딘/중고나라/번개장터)에 등록 자동화 (Selenium)
- 모든 데이터는 로컬 SQLite에만 저장, 외부로 안 나감

전체 요구사항은 PRD 참고 (같이 첨부한 `book-resale-prd.html` 또는 별도 공유 예정).

## 기술 스택 (확정)
| 영역 | 선택 | 비고 |
|---|---|---|
| 백엔드 | Spring Boot **4.1.1** | Java 17 기준으로 생성 (21 업그레이드는 보류, 지금은 17 유지) |
| 언어 | Java | |
| DB | SQLite (`booklegder.db`) | JPA/Hibernate 사용, dialect는 `org.hibernate.community.dialect.SQLiteDialect` |
| 사이트 자동화 | Selenium | 3단계에서 진행 예정, 아직 미착수 |
| 외부 API | 알라딘 Open API | TTBKey 발급 완료, 환경변수 `ALADIN_TTB_KEY`로 주입 |
| 빌드 | Gradle | `hibernate-community-dialects`는 버전 명시하지 않고 Spring Boot BOM에 맡김 |

패키지 루트: `com.booksefter.book_ledger`

## 로드맵 & 현재 위치
1. **[진행 중] 1단계 — 핵심 조회 & 중복 체크**
   - [x] Spring Boot 프로젝트 생성, 빌드/실행 확인 완료
   - [x] SQLite 연결 확인 완료 (Hikari + Hibernate 정상 동작 로그 확인)
   - [x] `BookStatus` enum 작성
   - [x] `Book` 엔티티 작성 (`isbn` PK, `title`, `author`, `coverUrl`, `listPrice`, `status`, `createdAt`)
   - [x] `BookRepository` (JpaRepository) 작성
   - [x] `BookController` — `GET /api/books`, `POST /api/books` (중복 시 409 반환) 작성
   - [ ] curl로 등록/중복체크 동작 테스트 (진행 예정)
   - [ ] 알라딘 Open API 클라이언트 작성 (도서 정보 + 매입 시세 조회)
   - [ ] `PriceHistory` 엔티티/테이블 추가
2. **2단계 — 대시보드 연결**: 기존에 만든 HTML 대시보드 목업을 정적 리소스로 붙이고 REST API 연결
3. **3단계 — 사이트 자동화 (1개 사이트부터)**: `SiteAdapter` 인터페이스 정의, 계정 리스크 낮은 곳부터 Selenium 적용
4. **4단계 — 다중 사이트 확장**: 재시도 큐, 실패 알림 등

## 아직 정하지 않은 것 (오픈 이슈)
- 알라딘 "매입 신청 접수"까지 API로 가능한지 확인 필요 (3단계에서 결정)
- 당근마켓은 공식 API 없음 → 우선순위 낮음, 나중에 검토
- 완전 자동 등록 vs 반자동(폼만 채우고 최종 클릭은 사람) — 사이트별로 다시 결정 예정, 지금은 반자동 쪽에 무게

## 코드 스타일 / 컨벤션 메모
- 이 프로젝트는 1인 개인용 로컬 도구 — 과도한 엔터프라이즈 패턴(불필요한 추상화 레이어 등)은 지양
- 로그인 세션/쿠키 등 민감 정보는 절대 평문 저장 금지 (3단계에서 Jasypt 등으로 암호화 예정)
- `.env` 또는 환경변수로 시크릿 관리, `application.properties`에 직접 값 하드코딩 금지

## 다음 작업 제안
1단계 마무리: 알라딘 Open API 클라이언트 작성 → `PriceLookupService`에서 ISBN으로 도서 정보 + 매입가 조회 → `Book` 등록 시 자동으로 시세 채워넣기
