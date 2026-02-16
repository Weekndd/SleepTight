# SleepTight

<div align="center">

![Flutter](https://img.shields.io/badge/Flutter-3.7.2-02569B)
![NestJS](https://img.shields.io/badge/NestJS-11.0-E0234E)
![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13-4169E1)
![Redis](https://img.shields.io/badge/Redis-Latest-DC382D)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

**AI 기반 수면 사운드 분석과 RAG 코칭을 제공하는 통합 수면 관리 애플리케이션**

[시스템 아키텍처](#3-시스템-아키텍처) |
[기술 스택](#2-기술-스택) |
[설치 가이드](#8-설치-및-실행)

</div>

---

## 목차

- [1. 프로젝트 개요](#1-프로젝트-개요)
- [2. 기술 스택](#2-기술-스택)
- [3. 시스템 아키텍처](#3-시스템-아키텍처)
- [4. 주요 기능](#4-주요-기능)
- [5. 프로젝트 구조](#5-프로젝트-구조)
- [6. 데이터베이스 설계](#6-데이터베이스-설계)
- [7. API 명세](#7-api-명세)
- [8. 설치 및 실행](#8-설치-및-실행)
- [9. 배포 가이드](#9-배포-가이드)
- [10. 개발 가이드](#10-개발-가이드)

---

## 1. 프로젝트 개요

### 1.1 프로젝트 소개

SleepTight는 사용자의 수면 패턴을 분석하고 개인화된 수면 코칭을 제공하는 크로스 플랫폼 애플리케이션입니다. 수면 중 발생하는 소리(코골이, 이갈이 등)를 딥러닝 모델로 분석하고, Wear OS 기기와 연동하여 활동량 데이터를 수집합니다. RAG(Retrieval Augmented Generation) 기반의 AI 코칭 시스템을 통해 사용자 맞춤형 수면 개선 가이드를 제공합니다.

### 1.2 핵심 가치

- **정확한 수면 분석**: 수면 단계 추적 및 수면 품질 점수 산출
- **AI 사운드 분석**: 딥러닝 기반 수면 중 이상 소리 감지 및 분류
- **개인화된 코칭**: RAG 기반 LLM을 활용한 맞춤형 수면 개선 조언
- **멀티 디바이스 연동**: 스마트폰과 Wear OS 기기 간 실시간 데이터 동기화

### 1.3 주요 특징

- **다중 플랫폼 지원**: Flutter 기반 Android/iOS 앱, Kotlin 기반 Wear OS 앱
- **비동기 사운드 처리**: RabbitMQ를 통한 음성 파일 비동기 분석 파이프라인
- **Blue-Green 배포**: 무중단 배포를 통한 서비스 연속성 보장
- **GPU 가속 추론**: RunPod을 활용한 ML 모델 추론 성능 최적화
- **분산 저장**: AWS S3와 연동한 대용량 오디오 파일 저장
- **벡터 검색**: Pinecone을 활용한 수면 관련 지식 검색

---

## 2. 기술 스택

### 2.1 Mobile Application

| 기술 | 버전 | 용도 |
|------|------|------|
| **Flutter** | 3.7.2 | 크로스 플랫폼 모바일 앱 프레임워크 |
| **Dart** | 3.x | 메인 개발 언어 |
| **Riverpod** | 2.6.1 | 상태 관리 (코드 생성 방식) |
| **Go Router** | 15.1.1 | 선언적 라우팅 |
| **Dio** | 5.8.0 | HTTP 클라이언트 |
| **Flutter Sound** | 9.28.0 | 오디오 녹음 |
| **Just Audio** | 0.9.46 | 오디오 재생 |
| **Health** | 12.2.0 | iOS HealthKit 연동 |
| **Firebase Messaging** | 15.2.6 | 푸시 알림 |
| **Kakao Flutter SDK** | 1.9.7 | 카카오 소셜 로그인 |

### 2.2 Wear OS Application

| 기술 | 버전 | 용도 |
|------|------|------|
| **Kotlin** | 1.9.x | 메인 개발 언어 |
| **Jetpack Compose** | Latest | 선언적 UI 프레임워크 |
| **Wearable Data Layer API** | Latest | 스마트폰-워치 간 통신 |
| **Health Services** | Latest | 심박수, 걸음 수 수집 |

### 2.3 Backend API Server

| 기술 | 버전 | 용도 |
|------|------|------|
| **NestJS** | 11.0.1 | 백엔드 프레임워크 |
| **TypeScript** | 5.7.3 | 메인 개발 언어 |
| **TypeORM** | 11.0.0 | 데이터베이스 ORM |
| **PostgreSQL** | 13 | 메인 관계형 데이터베이스 |
| **Redis** | Latest | 세션 캐시 |
| **RabbitMQ** | 3.x | 메시지 큐 (AI 서비스 연동) |
| **Passport JWT** | 4.0.1 | JWT 인증 |
| **Swagger** | 11.1.6 | API 문서화 |
| **AWS S3 SDK** | 3.8.0 | 파일 스토리지 |
| **Firebase Admin** | 13.4.0 | 푸시 알림 발송 |

### 2.4 AI Coaching Service

| 기술 | 버전 | 용도 |
|------|------|------|
| **FastAPI** | 0.115.12 | AI 서비스 프레임워크 |
| **LangChain** | 0.3.25 | LLM 오케스트레이션 |
| **OpenAI** | 1.78.0 | GPT 모델 API |
| **Pinecone** | 6.0.2 | 벡터 데이터베이스 |
| **Tiktoken** | 0.9.0 | 토큰 카운팅 |

### 2.5 Sound Analysis Service

| 기술 | 버전 | 용도 |
|------|------|------|
| **FastAPI** | 0.115.12 | AI 서비스 프레임워크 |
| **PyTorch** | 2.x | 딥러닝 프레임워크 |
| **Transformers** | 4.51.3 | 사전 학습 모델 |
| **Pydub** | 0.25.1 | 오디오 파일 처리 |
| **Soundfile** | 0.13.1 | 오디오 I/O |
| **Pika** | 1.3.2 | RabbitMQ 클라이언트 |
| **Boto3** | 1.38.10 | AWS S3 연동 |

### 2.6 Infrastructure & DevOps

| 기술 | 용도 |
|------|------|
| **Docker** | 컨테이너화 |
| **Docker Compose** | 멀티 컨테이너 오케스트레이션 |
| **Nginx** | 리버스 프록시, 로드 밸런싱 |
| **Jenkins** | CI/CD 파이프라인 |
| **AWS S3** | 오디오 파일 저장 |
| **RunPod** | GPU 인스턴스 (ML 추론) |
| **GitLab** | 버전 관리 |

---

## 3. 시스템 아키텍처

### 3.1 전체 아키텍처

![시스템 아키텍처](doc/system%20architecture.png)

### 3.2 수면 분석 시퀀스 다이어그램

![수면 분석 시퀀스 다이어그램](doc/sequence%20diagram.png)

---

## 4. 주요 기능

### 4.1 수면 모드

| 기능 | 설명 |
|------|------|
| 수면 시작/종료 | 사용자 수면 시간 기록 및 자동 계산 |
| 오디오 녹음 | 수면 중 주기적인 음성 녹음 (청크 단위) |
| 백그라운드 실행 | 앱이 백그라운드에서도 지속적인 녹음 유지 |
| 알람 설정 | 수면 종료 알람 및 스마트 알람 |

### 4.2 수면 분석

| 기능 | 설명 |
|------|------|
| 수면 단계 분석 | 깊은 수면, 얕은 수면, REM 수면 단계 구분 |
| 수면 품질 점수 | 수면 효율, 수면 시간 기반 점수 산출 |
| 주간/월간 리포트 | 기간별 수면 패턴 시각화 |
| 캘린더 뷰 | 날짜별 수면 기록 조회 |

### 4.3 수면 사운드 분석

| 기능 | 설명 |
|------|------|
| 이상 소리 감지 | AI 기반 코골이, 이갈이, 수면 무호흡 감지 |
| 이벤트 클립 | 감지된 소리 구간 자동 클리핑 |
| 클립 재생 | 감지된 이벤트 오디오 재생 |
| 통계 제공 | 이벤트 유형별 발생 횟수 및 시간대 분석 |

### 4.4 AI 수면 코칭

| 기능 | 설명 |
|------|------|
| 개인화 분석 | 사용자 수면 패턴 및 활동량 기반 분석 |
| RAG 기반 조언 | 수면 과학 지식 기반 맞춤형 코칭 |
| 실천 가이드 | 구체적인 수면 개선 행동 제안 |
| 히스토리 관리 | 코칭 기록 저장 및 조회 |

### 4.5 Wear OS 연동

| 기능 | 설명 |
|------|------|
| 활동량 동기화 | 걸음 수, 소모 칼로리 실시간 수집 |
| 심박수 모니터링 | 수면 중 심박수 데이터 수집 |
| 양방향 통신 | 스마트폰-워치 간 데이터 교환 |
| 독립 실행 | 워치 단독 데이터 조회 |

### 4.6 부가 기능

| 기능 | 설명 |
|------|------|
| 수면 유도 음악 | 카테고리별 백색소음, 자연 소리 재생 |
| 수면 일기 | 주관적 수면 평가 및 메모 작성 |
| 카카오 로그인 | 소셜 인증을 통한 간편 로그인 |
| 푸시 알림 | 수면 리마인더, 코칭 알림 |

---

## 5. 프로젝트 구조

### 5.1 전체 디렉토리 구조

```
SleepTight/
├── client/
│   ├── app/                              # Flutter 모바일 앱
│   └── wear/                             # Wear OS 앱
│
├── server/
│   ├── api/                              # NestJS 백엔드 API
│   ├── ai/                               # AI 코칭 서비스 (FastAPI)
│   └── sound-ai/                         # 사운드 분석 서비스 (FastAPI)
│
├── cicd/                                 # CI/CD 설정
├── doc/                                  # 문서 및 산출물
└── exec/                                 # 실행 관련 파일
```

### 5.2 Flutter App 구조

```
client/app/
├── lib/
│   ├── core/                             # 공통 설정 및 인프라
│   │   ├── config/
│   │   │   ├── app_config.dart           # 앱 설정 (API URL 등)
│   │   │   ├── router.dart               # Go Router 설정
│   │   │   └── theme/                    # 테마 설정
│   │   │       ├── color.dart            # 색상 정의
│   │   │       ├── text_styles.dart      # 텍스트 스타일
│   │   │       └── theme.dart            # 테마 데이터
│   │   ├── network/
│   │   │   ├── dio_client.dart           # Dio HTTP 클라이언트
│   │   │   ├── dio_provider.dart         # Dio Riverpod Provider
│   │   │   ├── api_interceptor.dart      # 요청/응답 인터셉터
│   │   │   └── api_error_handler.dart    # 에러 처리
│   │   ├── service/
│   │   │   ├── fcm_messaging_service.dart # FCM 푸시 알림
│   │   │   └── alarm_service.dart        # 알람 서비스
│   │   ├── storage/
│   │   │   ├── secure_storage_provider.dart   # 보안 저장소 (토큰)
│   │   │   └── shared_preferences_provider.dart # 로컬 설정
│   │   ├── state/navigation/             # 네비게이션 상태
│   │   ├── data/models/                  # 공통 모델
│   │   ├── error/                        # 예외 클래스
│   │   └── utils/                        # 유틸리티 함수
│   │
│   └── features/                         # 기능별 모듈 (Clean Architecture)
│       ├── auth/                         # 인증 기능
│       │   ├── data/
│       │   │   ├── datasources/          # 로컬/원격 데이터 소스
│       │   │   ├── models/               # DTO (Request/Response)
│       │   │   └── repositories/         # Repository 구현체
│       │   ├── domain/
│       │   │   └── repositories/         # Repository 인터페이스
│       │   └── presentation/
│       │       ├── providers/            # Riverpod Provider
│       │       ├── screens/              # 화면 Widget
│       │       └── widgets/              # UI 컴포넌트
│       │
│       ├── sleep_mode/                   # 수면 모드
│       │   ├── data/
│       │   │   ├── datasources/          # 수면 데이터 소스
│       │   │   ├── models/               # 수면 모델
│       │   │   └── services/             # 녹음 서비스
│       │   └── presentation/
│       │       ├── providers/            # 수면 상태 관리
│       │       ├── screens/              # 수면 모드 화면
│       │       └── widgets/              # 수면 UI 컴포넌트
│       │
│       ├── analysis/                     # 수면 분석
│       │   ├── data/
│       │   │   ├── datasources/          # 리포트 데이터 소스
│       │   │   ├── models/               # 리포트 모델
│       │   │   └── services/             # 리포트 서비스
│       │   ├── domain/
│       │   │   ├── entity/               # 도메인 엔티티
│       │   │   └── repositories/         # Repository 인터페이스
│       │   └── presentation/
│       │       ├── providers/            # 분석 상태 관리
│       │       ├── screens/              # 분석 화면
│       │       └── widgets/              # 차트, 캘린더 등
│       │
│       ├── coach/                        # AI 코칭
│       │   ├── data/                     # 코칭 데이터 레이어
│       │   ├── domain/                   # 코칭 도메인 레이어
│       │   └── presentation/             # 코칭 UI 레이어
│       │
│       ├── music/                        # 수면 음악
│       │   ├── data/                     # 음악 데이터 레이어
│       │   ├── domain/                   # 음악 도메인 레이어
│       │   └── presentation/
│       │       ├── providers/            # 오디오 컨트롤러
│       │       └── widgets/              # 플레이어 UI
│       │
│       ├── health/                       # 헬스 데이터
│       │   ├── models/                   # 수면 데이터 모델
│       │   └── services/
│       │       ├── health_service.dart   # HealthKit 연동
│       │       └── wear_communication_service.dart # Wear OS 연동
│       │
│       └── user/                         # 사용자 프로필
│           ├── data/                     # 사용자 데이터 레이어
│           └── presentation/             # 프로필 화면
│
├── assets/
│   ├── images/                           # 이미지 리소스
│   ├── icons/                            # 아이콘 리소스
│   ├── fonts/                            # 폰트 파일 (Pretendard 등)
│   └── sound/                            # 오디오 리소스
│
├── pubspec.yaml                          # Flutter 의존성
└── .env                                  # 환경 변수
```

### 5.3 Wear OS App 구조

```
client/wear/
├── app/src/main/java/com/example/sleeptight/wear/
│   ├── SleepTightApplication.kt          # Application 클래스
│   ├── data/
│   │   ├── model/
│   │   │   └── HealthData.kt             # 헬스 데이터 모델
│   │   ├── repository/
│   │   │   └── WearableRepository.kt     # 데이터 저장소
│   │   ├── service/
│   │   │   └── WearableMessageService.kt # 메시지 서비스
│   │   └── util/
│   │       └── ConnectionChecker.kt      # 연결 상태 확인
│   └── presentation/
│       ├── MainActivity.kt               # 메인 액티비티
│       ├── SplashActivity.kt             # 스플래시 화면
│       ├── viewmodel/
│       │   └── HealthViewModel.kt        # 헬스 뷰모델
│       ├── components/
│       │   ├── MainScreen.kt             # 메인 화면
│       │   ├── MetricPage.kt             # 지표 페이지
│       │   └── chart/
│       │       └── CircularProgressBar.kt # 원형 프로그레스
│       └── theme/
│           └── SleepTightTheme.kt        # 앱 테마
│
├── app/src/main/res/                     # 리소스 파일
├── build.gradle.kts                      # Gradle 빌드 설정
└── gradle/libs.versions.toml             # 버전 카탈로그
```

### 5.4 Backend API 구조

```
server/api/
├── src/
│   ├── main.ts                           # 엔트리 포인트
│   ├── app.module.ts                     # 루트 모듈
│   │
│   ├── auth/                             # 인증 모듈
│   │   ├── auth.module.ts
│   │   ├── auth.controller.ts            # 로그인/로그아웃 API
│   │   ├── auth.service.ts               # 인증 비즈니스 로직
│   │   ├── jwt.strategy.ts               # JWT 전략
│   │   └── jwt-auth.guard.ts             # JWT 가드
│   │
│   ├── users/                            # 사용자 모듈
│   │   ├── user.module.ts
│   │   ├── user.controller.ts            # 사용자 API
│   │   ├── user.service.ts               # 사용자 서비스
│   │   ├── entities/
│   │   │   └── user.entity.ts            # User 엔티티
│   │   └── dto/                          # DTO 정의
│   │
│   ├── sleep-reports/                    # 수면 리포트 모듈
│   │   ├── sleep-report.module.ts
│   │   ├── sleep-report.controller.ts    # 리포트 API
│   │   ├── sleep-report.service.ts       # 리포트 서비스
│   │   ├── sleep-diaries.controller.ts   # 수면 일기 API
│   │   ├── sleep-diaries.service.ts      # 일기 서비스
│   │   ├── entities/
│   │   │   ├── sleep-report.entity.ts    # SleepReport 엔티티
│   │   │   ├── sleep-diary.entity.ts     # SleepDiary 엔티티
│   │   │   └── sleep-stage-log.entity.ts # SleepStageLog 엔티티
│   │   └── dto/
│   │
│   ├── sleep-sound/                      # 수면 사운드 모듈
│   │   ├── sleep-sound.module.ts
│   │   ├── sleep-sound.controller.ts     # 사운드 업로드 API
│   │   ├── sleep-sound.service.ts        # 사운드 서비스
│   │   ├── sleep-sound.producer.ts       # RabbitMQ 프로듀서
│   │   ├── sleep-analysis-result.listener.ts # 분석 결과 리스너
│   │   └── entities/
│   │       ├── sleep-sound.entity.ts     # SleepSound 엔티티
│   │       └── sleep-event.entity.ts     # SleepEvent 엔티티
│   │
│   ├── sleep-coaching/                   # 수면 코칭 모듈
│   │   ├── sleep-coaching.module.ts
│   │   ├── sleep-coaching.controller.ts  # 코칭 API
│   │   ├── sleep-coaching.service.ts     # 코칭 서비스
│   │   ├── activity-data.controller.ts   # 활동량 API
│   │   ├── activity-data.service.ts      # 활동량 서비스
│   │   └── entities/
│   │       ├── sleep-coaching.entity.ts  # SleepCoaching 엔티티
│   │       └── activity-data.entity.ts   # ActivityData 엔티티
│   │
│   ├── music/                            # 음악 모듈
│   │   ├── music.module.ts
│   │   ├── music.controller.ts           # 음악 API
│   │   ├── music.service.ts              # 음악 서비스
│   │   └── music.entity.ts               # Music 엔티티
│   │
│   ├── health/                           # 헬스 체크 모듈
│   │   ├── health.module.ts
│   │   └── health.controller.ts          # 헬스 체크 API
│   │
│   ├── task/                             # 스케줄러 모듈
│   │   ├── task.module.ts
│   │   └── task.service.ts               # 스케줄 작업
│   │
│   └── common/                           # 공통 모듈
│       ├── aws/
│       │   ├── s3.module.ts              # S3 모듈
│       │   └── s3.provider.ts            # S3 클라이언트
│       ├── fcm/
│       │   ├── fcm.module.ts             # FCM 모듈
│       │   └── fcm.service.ts            # FCM 서비스
│       ├── config/
│       │   └── swagger.ts                # Swagger 설정
│       ├── filters/
│       │   └── http-exception.filter.ts  # 예외 필터
│       ├── interceptors/
│       │   └── response.interceptor.ts   # 응답 인터셉터
│       └── exceptions/
│           ├── exception-code.enum.ts    # 예외 코드
│           └── exception.helper.ts       # 예외 헬퍼
│
├── package.json                          # NPM 의존성
├── nest-cli.json                         # NestJS CLI 설정
└── tsconfig.json                         # TypeScript 설정
```

### 5.5 AI Services 구조

```
server/ai/app/                            # AI 코칭 서비스
├── main.py                               # FastAPI 엔트리 포인트
├── config.py                             # 환경 설정
├── routers/
│   └── coaching.py                       # 코칭 API 라우터
├── models/
│   ├── request.py                        # 요청 모델
│   ├── response.py                       # 응답 모델
│   └── suggestion.py                     # 제안 모델
├── prompts/
│   └── prompt.py                         # LLM 프롬프트 템플릿
├── utils/
│   └── text_utils.py                     # 텍스트 처리 유틸
├── requirements.txt                      # Python 의존성
└── Dockerfile                            # 도커 이미지

server/sound-ai/app/                      # 사운드 분석 서비스
├── main.py                               # FastAPI 엔트리 포인트
├── config.py                             # 환경 설정
├── consumer.py                           # RabbitMQ 컨슈머
├── model/
│   └── model.pth                         # 학습된 PyTorch 모델
├── utils/
│   ├── audio.py                          # 오디오 처리
│   ├── inference.py                      # 모델 추론
│   └── s3_client.py                      # S3 클라이언트
├── requirements.txt                      # Python 의존성
└── Dockerfile                            # 도커 이미지
```

### 5.6 CI/CD 구조

```
cicd/
├── docker-compose.yml                    # 애플리케이션 서비스
├── docker-compose-infra.yml              # 인프라 서비스
├── backend.Jenkinsfile                   # 백엔드 CI/CD 파이프라인
├── ai-service.Jenkinsfile                # AI 서비스 CI/CD 파이프라인
├── infra.Jenkinsfile                     # 인프라 파이프라인
├── deploy_backend.sh                     # 백엔드 배포 스크립트
└── deploy_ai.sh                          # AI 서비스 배포 스크립트
```

---

## 6. 데이터베이스 설계

### 6.1 주요 엔티티

#### User (사용자)
```
- id (PK, UUID)
- kakao_id (UNIQUE)
- name
- email
- profile_image_url
- birth_date
- gender
- height
- weight
- fcm_token
- created_at, updated_at
```

#### SleepReport (수면 리포트)
```
- id (PK, UUID)
- user_id (FK → User)
- sleep_start_at
- sleep_end_at
- total_sleep_minutes
- sleep_score
- created_at, updated_at
```

#### SleepStageLog (수면 단계 로그)
```
- id (PK, UUID)
- sleep_report_id (FK → SleepReport)
- stage_type (ENUM: AWAKE, LIGHT, DEEP, REM)
- start_at
- end_at
- duration_minutes
```

#### SleepDiary (수면 일기)
```
- id (PK, UUID)
- sleep_report_id (FK → SleepReport)
- wake_method (ENUM)
- wake_awareness (ENUM)
- memo
- created_at, updated_at
```

#### SleepSound (수면 사운드)
```
- id (PK, UUID)
- sleep_report_id (FK → SleepReport)
- file_path (S3 Key)
- file_name
- duration_seconds
- analysis_status (ENUM: PENDING, PROCESSING, COMPLETED, FAILED)
- created_at, updated_at
```

#### SleepEvent (수면 이벤트)
```
- id (PK, UUID)
- sleep_sound_id (FK → SleepSound)
- event_type (ENUM: SNORING, TEETH_GRINDING, SLEEP_APNEA)
- start_time
- end_time
- confidence_score
- clip_file_path
```

#### SleepCoaching (수면 코칭)
```
- id (PK, UUID)
- user_id (FK → User)
- coaching_type (ENUM)
- content (TEXT)
- created_at
```

#### ActivityData (활동 데이터)
```
- id (PK, UUID)
- user_id (FK → User)
- data_type (ENUM: STEPS, CALORIES, CAFFEINE, WATER)
- value
- unit
- recorded_at
- created_at
```

#### Music (음악)
```
- id (PK, UUID)
- title
- category (ENUM: WHITE_NOISE, NATURE, ASMR, MUSIC)
- file_path
- thumbnail_path
- duration_seconds
- created_at
```

### 6.2 엔티티 관계도

```
User ─────┬───── SleepReport (1:N)
          │            │
          │            ├───── SleepStageLog (1:N)
          │            │
          │            ├───── SleepDiary (1:1)
          │            │
          │            └───── SleepSound (1:N)
          │                        │
          │                        └───── SleepEvent (1:N)
          │
          ├───── SleepCoaching (1:N)
          │
          └───── ActivityData (1:N)

Music (독립 엔티티)
```

### 6.3 인덱스

```sql
-- 사용자별 수면 리포트 조회
CREATE INDEX idx_sleep_report_user_id ON sleep_reports(user_id);
CREATE INDEX idx_sleep_report_sleep_start ON sleep_reports(sleep_start_at);

-- 수면 리포트별 사운드 조회
CREATE INDEX idx_sleep_sound_report_id ON sleep_sounds(sleep_report_id);
CREATE INDEX idx_sleep_sound_analysis_status ON sleep_sounds(analysis_status);

-- 사운드별 이벤트 조회
CREATE INDEX idx_sleep_event_sound_id ON sleep_events(sleep_sound_id);
CREATE INDEX idx_sleep_event_type ON sleep_events(event_type);

-- 활동 데이터 조회
CREATE INDEX idx_activity_data_user_id ON activity_data(user_id);
CREATE INDEX idx_activity_data_recorded_at ON activity_data(recorded_at);
```

---

## 7. API 명세

### 7.1 Swagger UI 접근

```
http://localhost:3000/api/docs
```

### 7.2 인증 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/auth/kakao` | 카카오 로그인 | Public |
| POST | `/auth/refresh` | 토큰 갱신 | Public |
| POST | `/auth/logout` | 로그아웃 | User |

### 7.3 사용자 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/users/me` | 내 정보 조회 | User |
| PUT | `/users/me` | 내 정보 수정 | User |
| PUT | `/users/me/sleep-preferences` | 수면 설정 수정 | User |
| DELETE | `/users/me` | 회원 탈퇴 | User |

### 7.4 수면 리포트 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/sleep-reports/start` | 수면 시작 | User |
| POST | `/sleep-reports/end` | 수면 종료 | User |
| GET | `/sleep-reports` | 리포트 목록 조회 | User |
| GET | `/sleep-reports/:id` | 리포트 상세 조회 | User |
| GET | `/sleep-reports/calendar` | 캘린더 조회 | User |

### 7.5 수면 일기 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/sleep-diaries/:reportId` | 일기 조회 | User |
| PUT | `/sleep-diaries/:reportId` | 일기 수정 | User |

### 7.6 수면 사운드 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/sleep-sounds/upload` | 사운드 업로드 | User |
| GET | `/sleep-sounds/:reportId` | 사운드 목록 조회 | User |
| GET | `/sleep-sounds/:id/events` | 이벤트 목록 조회 | User |
| GET | `/sleep-sounds/events/:id/clip` | 이벤트 클립 URL | User |

### 7.7 수면 코칭 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/sleep-coaching` | 코칭 요청 | User |
| GET | `/sleep-coaching` | 코칭 기록 조회 | User |

### 7.8 활동 데이터 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/activity-data` | 활동 데이터 저장 | User |
| GET | `/activity-data` | 활동 데이터 조회 | User |

### 7.9 음악 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/music` | 음악 목록 조회 | User |
| GET | `/music/:id` | 음악 상세 조회 | User |
| GET | `/music/:id/stream` | 음악 스트리밍 URL | User |

### 7.10 요청/응답 예시

#### 수면 시작 (POST /sleep-reports/start)

**Request**
```json
{
  "sleepStartAt": "2025-02-05T23:30:00+09:00"
}
```

**Response** (201 Created)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-uuid",
  "sleepStartAt": "2025-02-05T23:30:00+09:00",
  "createdAt": "2025-02-05T23:30:00+09:00"
}
```

#### 수면 리포트 조회 (GET /sleep-reports/:id)

**Response** (200 OK)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "sleepStartAt": "2025-02-05T23:30:00+09:00",
  "sleepEndAt": "2025-02-06T07:00:00+09:00",
  "totalSleepMinutes": 450,
  "sleepScore": 85,
  "stages": [
    {
      "stageType": "LIGHT",
      "startAt": "2025-02-05T23:30:00+09:00",
      "endAt": "2025-02-05T23:50:00+09:00",
      "durationMinutes": 20
    },
    {
      "stageType": "DEEP",
      "startAt": "2025-02-05T23:50:00+09:00",
      "endAt": "2025-02-06T01:30:00+09:00",
      "durationMinutes": 100
    }
  ],
  "soundEvents": [
    {
      "eventType": "SNORING",
      "startTime": "2025-02-06T02:15:30+09:00",
      "endTime": "2025-02-06T02:16:45+09:00",
      "confidenceScore": 0.92
    }
  ]
}
```

---

## 8. 설치 및 실행

### 8.1 사전 요구사항

| 도구 | 버전 | 용도 |
|------|------|------|
| Flutter SDK | 3.7.2+ | 모바일 앱 개발 |
| Node.js | 18+ | 백엔드 API |
| Python | 3.10+ | AI 서비스 |
| Docker | Latest | 컨테이너 실행 |
| Android Studio | Latest | Wear OS 개발 |
| PostgreSQL | 13+ | 데이터베이스 |
| Redis | 6+ | 캐시 |
| RabbitMQ | 3+ | 메시지 큐 |

### 8.2 Flutter 앱 실행

```bash
# 프로젝트 디렉토리 이동
cd client/app

# 의존성 설치
flutter pub get

# 코드 생성 (Riverpod providers)
flutter packages pub run build_runner build

# 환경 변수 설정
cp .env.example .env
# .env 파일 수정

# 개발 모드 실행
flutter run

# Android APK 빌드
flutter build apk --release

# iOS 빌드
flutter build ios --release
```

### 8.3 Wear OS 앱 실행

```bash
# 프로젝트 디렉토리 이동
cd client/wear

# Gradle 빌드
./gradlew build

# 연결된 Wear OS 기기에 설치
./gradlew installDebug

# Release APK 빌드
./gradlew assembleRelease
```

### 8.4 백엔드 API 실행

```bash
# 프로젝트 디렉토리 이동
cd server/api

# 의존성 설치
npm install

# 환경 변수 설정
cp .env.example .env
# .env 파일 수정

# 개발 모드 실행
npm run start:dev

# 프로덕션 빌드
npm run build
npm run start:prod

# 테스트 실행
npm test
npm run test:e2e
```

### 8.5 AI 코칭 서비스 실행

```bash
# 프로젝트 디렉토리 이동
cd server/ai/app

# 가상환경 생성 및 활성화
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate

# 의존성 설치
pip install -r requirements.txt

# 환경 변수 설정
export OPENAI_API_KEY=your_api_key
export PINECONE_API_KEY=your_api_key

# 서버 실행
uvicorn main:app --reload --host 0.0.0.0 --port 8081
```

### 8.6 사운드 분석 서비스 실행

```bash
# 프로젝트 디렉토리 이동
cd server/sound-ai/app

# 가상환경 생성 및 활성화
python -m venv venv
source venv/bin/activate

# 의존성 설치
pip install -r requirements.txt

# 환경 변수 설정
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
export RABBITMQ_URL=amqp://localhost:5672

# 서버 실행
uvicorn main:app --reload --host 0.0.0.0 --port 8082
```

### 8.7 환경 변수 설정

#### Backend API (.env)

```env
# Server
PORT=3000
NODE_ENV=development

# Database
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_DATABASE=sleeptight

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRES_IN=7d

# AWS S3
AWS_REGION=ap-northeast-2
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_S3_BUCKET=sleeptight-bucket

# RabbitMQ
RABBITMQ_URL=amqp://localhost:5672

# Firebase
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_PRIVATE_KEY=your_private_key
FIREBASE_CLIENT_EMAIL=your_client_email

# AI Service
AI_SERVICE_URL=http://localhost:8081
```

#### AI Coaching Service (.env)

```env
# OpenAI
OPENAI_API_KEY=your_openai_api_key

# Pinecone
PINECONE_API_KEY=your_pinecone_api_key
PINECONE_INDEX_NAME=sleep-knowledge
PINECONE_ENVIRONMENT=us-east-1
```

#### Flutter App (.env)

```env
API_BASE_URL=https://api.sleeptight.com
KAKAO_NATIVE_APP_KEY=your_kakao_app_key
```

---

## 9. 배포 가이드

### 9.1 Docker 인프라 배포

```bash
# cicd 디렉토리 이동
cd cicd

# Docker 네트워크 생성
docker network create app-network

# 인프라 서비스 실행 (PostgreSQL, Redis, RabbitMQ)
docker-compose -f docker-compose-infra.yml up -d

# 상태 확인
docker-compose -f docker-compose-infra.yml ps
```

### 9.2 애플리케이션 배포

```bash
# 애플리케이션 서비스 실행
docker-compose -f docker-compose.yml up -d

# 로그 확인
docker-compose -f docker-compose.yml logs -f
```

### 9.3 Blue-Green 배포

```
배포 프로세스:
1. Green 인스턴스에 새 버전 배포
2. 헬스 체크 통과 확인
3. Nginx 설정 변경 (Blue → Green)
4. Blue 인스턴스 대기 상태로 전환
```

#### Nginx 설정 예시

```nginx
upstream backend {
    server backend-blue:3000 weight=1;
    server backend-green:3000 weight=0 backup;
}

server {
    listen 80;
    server_name api.sleeptight.com;

    location / {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 9.4 Jenkins 파이프라인

```groovy
// backend.Jenkinsfile
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://gitlab.com/sleeptight/server.git'
            }
        }

        stage('Build') {
            steps {
                dir('server/api') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t sleeptight-backend:latest ./server/api'
            }
        }

        stage('Deploy') {
            steps {
                sh './cicd/deploy_backend.sh'
            }
        }
    }
}
```

### 9.5 RunPod GPU 배포 (Sound Analysis)

```bash
# RunPod 템플릿 설정
# - GPU: NVIDIA A10G 또는 RTX 4090
# - Container Image: sleeptight/sound-ai:latest
# - Environment Variables 설정

# 배포 명령
runpodctl create pod \
  --name sleeptight-sound-ai \
  --image sleeptight/sound-ai:latest \
  --gpu-type "NVIDIA A10G" \
  --env RABBITMQ_URL=amqp://your-host:5672
```

---

## 10. 개발 가이드

### 10.1 코딩 컨벤션

#### Flutter

```
- Clean Architecture 패턴 준수
- Feature 단위 폴더 구조
- Riverpod 코드 생성 방식 사용 (@riverpod)
- 네이밍: snake_case (파일), camelCase (변수/함수), PascalCase (클래스)
```

#### NestJS

```
- Module 기반 구조
- DTO를 통한 요청/응답 검증
- Repository 패턴 적용
- 네이밍: camelCase (변수/함수), PascalCase (클래스), kebab-case (파일)
```

#### Python (AI Services)

```
- PEP 8 스타일 가이드 준수
- Type Hints 사용
- Pydantic 모델 활용
- 네이밍: snake_case (변수/함수/파일), PascalCase (클래스)
```

### 10.2 Git 브랜치 전략

```
main (master)
  └── dev/fe       # Frontend 개발
  └── dev/be       # Backend 개발
  └── dev/ai       # AI 서비스 개발
  └── feature/*    # 기능 개발
  └── hotfix/*     # 긴급 수정
```

### 10.3 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드, 설정 변경
```

### 10.4 트러블슈팅

#### 수면 사운드 분석 실패

```
문제: 사운드 분석이 PENDING 상태에서 멈춤
해결:
1. RabbitMQ 연결 상태 확인
2. Sound Analysis 서비스 로그 확인
3. S3 파일 접근 권한 확인
4. GPU 메모리 상태 확인
```

#### Flutter 빌드 오류

```
문제: Riverpod 코드 생성 오류
해결:
flutter clean
flutter pub get
flutter packages pub run build_runner build --delete-conflicting-outputs
```

#### JWT 인증 오류

```
문제: Token expired 에러
해결:
1. 토큰 만료 시간 확인
2. Refresh Token으로 갱신
3. 시간 동기화 확인 (서버/클라이언트)
```

---

<div align="center">

**SleepTight - Better Sleep, Better Life**

</div>
