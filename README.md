# Co-Work

> 팀 프로젝트 협업 플랫폼 — 팀 생성, 팀원 초대, 파일 공유, 공동 캘린더를 한 곳에서 관리합니다.

---

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [ERD](#erd)
- [API 명세](#api-명세)
- [실행 방법](#실행-방법)
  - [로컬 실행](#로컬-실행)
  - [Docker 실행](#docker-실행)
- [유저 유형 및 권한](#유저-유형-및-권한)
- [팀원](#팀원)

---

## 프로젝트 소개

Co-Work는 팀 단위 업무 협업을 위한 웹 애플리케이션입니다.  
팀을 생성하고 팀원을 초대하여 파일을 공유하고, 공동 캘린더로 일정을 함께 관리할 수 있습니다.

---

## 주요 기능

| 기능 | 설명 |
|---|---|
| 회원가입 / 로그인 | 이메일·비밀번호(BCrypt 암호화)·닉네임으로 가입 및 세션 기반 로그인 |
| 대시보드 | 참여 중인 팀 목록 조회 및 받은 초대 알림 수락·거절 |
| 팀 생성 | 팀 이름·설명 입력 후 생성, 생성 시 팀장으로 자동 등록 |
| 팀원 초대 | 이메일로 팀원 초대 발송, 초대 수락 시 팀 합류 |
| 팀원 강퇴 / 탈퇴 | 팀장은 팀원 강퇴, 일반 멤버는 팀 탈퇴 가능 |
| 파일 공유 | 팀 자료실에 파일 업로드·다운로드·삭제 (페이지당 10건) |
| 공동 캘린더 | 팀 일정 등록·수정·삭제, 상태 관리 (TODO / PROGRESS / DONE) |

---

## 기술 스택

**Backend**
- Java 17
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA (Hibernate 6)
- Thymeleaf

**Database**
- MySQL 8.0

**Build & Deploy**
- Maven
- Docker / Docker Compose

**Frontend**
- HTML / CSS / JavaScript
- Font Awesome 6.5.1

---

## 시스템 아키텍처

```
Browser
  │
  ▼
Spring Boot (Thymeleaf MVC)
  ├── Controller (세션 기반 인증)
  ├── Service
  └── Repository (Spring Data JPA)
        │
        ▼
     MySQL 8.0
```

---

## ERD
<img width="1440" height="1722" alt="image" src="https://github.com/user-attachments/assets/dc440515-1283-48bd-aeae-d931a847c6b1" />


## API 명세

### 인증

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/user/signup` | 회원가입 페이지 |
| POST | `/api/user/signup` | 회원가입 처리 |
| GET | `/api/user/login` | 로그인 페이지 |
| POST | `/api/user/login` | 로그인 처리 (세션 생성) |
| POST | `/api/user/logout` | 로그아웃 (세션 무효화) |

### 대시보드

| Method | URL | 설명 |
|---|---|---|
| GET | `/dashboard` | 내 팀 목록 + 초대 알림 조회 |
| POST | `/dashboard/invites/{teamId}/accept` | 초대 수락 |
| POST | `/dashboard/invites/{teamId}/decline` | 초대 거절 |

### 팀 관리

| Method | URL | 설명 |
|---|---|---|
| GET | `/teams/new` | 팀 생성 폼 |
| POST | `/teams/new` | 팀 생성 처리 |
| POST | `/teams/{teamId}/delete` | 팀 삭제 (팀장 전용) |
| POST | `/teams/{teamId}/leave` | 팀 탈퇴 (일반 멤버 전용) |
| GET | `/view/teams/{teamId}/members` | 팀 관리 페이지 (팀장 전용) |
| POST | `/view/teams/{teamId}/members/invite` | 팀원 초대 |
| POST | `/view/teams/{teamId}/members/remove` | 팀원 강퇴 / 초대 취소 |

### 파일 공유

| Method | URL | 설명 |
|---|---|---|
| GET | `/teams/{team_id}/files` | 파일 목록 조회 |
| POST | `/teams/{team_id}/files/upload` | 파일 업로드 |
| GET | `/teams/{team_id}/files/{file_id}/download` | 파일 다운로드 |
| POST | `/teams/{team_id}/files/{file_id}/delete` | 파일 삭제 |

### 공동 캘린더

| Method | URL | 설명 |
|---|---|---|
| GET | `/teams/{team_id}/calendar` | 캘린더 목록 조회 |
| POST | `/teams/{team_id}/calendar` | 일정 등록 |
| POST | `/teams/{team_id}/calendar/{task_id}/edit` | 일정 수정 |
| POST | `/teams/{team_id}/calendar/{task_id}/delete` | 일정 삭제 |

---

## 실행 방법

### 로컬 실행

**1. MySQL DB 생성**

```sql
CREATE DATABASE coop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**2. `application.properties` 수정**

`src/main/resources/application.properties`에서 본인 환경에 맞게 아래 항목만 변경합니다.

```properties
# DB 접속 정보 - 본인 환경에 맞게 변경
spring.datasource.url=jdbc:mysql://localhost:3306/coop_db?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=root           # 본인 MySQL 계정
spring.datasource.password=본인비밀번호    # 본인 MySQL 비밀번호

# 파일 업로드 경로 - OS에 맞게 변경 후 폴더 직접 생성 필요
# Mac / Linux
file.dir=/Users/본인계정명/uploads/
# Windows
file.dir=C:/uploads/
```

**3. 업로드 폴더 생성**

```bash
# Mac / Linux
mkdir -p ~/uploads

# Windows (cmd)
mkdir C:\uploads
```

**4. 실행**

```bash
mvn spring-boot:run
```

브라우저에서 `http://localhost:8080` 접속

---

### Docker 실행

**1. jar 빌드**

```bash
mvn clean package -DskipTests
```

**2. 필요 디렉토리 생성**

```bash
mkdir -p data/mysql data/db-init data/db-conf
```

**3. Docker Compose 실행**

```bash
docker-compose up --build
```

브라우저에서 `http://localhost:8080` 접속

**4. 종료**

```bash
# 컨테이너만 종료
docker-compose down

# 컨테이너 + 볼륨(DB 데이터) 전체 삭제
docker-compose down -v
```

> **주의:** `docker-compose.yml`의 DB 접속 URL은 `localhost` 대신 컨테이너명 `mysql-server`를 사용합니다.  
> `application.properties`는 수정하지 않아도 됩니다. Docker 환경 변수가 자동으로 덮어씁니다.

---

## 유저 유형 및 권한

| 유저 유형 | 설명 |
|---|---|
| 비회원 | 회원가입, 로그인 페이지만 접근 가능 |
| 팀 리더 | 팀 생성자. 팀원 초대·강퇴, 팀 삭제 권한 보유 |
| 팀 멤버 | 초대 수락 후 합류. 파일 업로드·다운로드, 캘린더 등록·수정·삭제, 팀 탈퇴 가능 |
| 초대 대기 | 초대장 수신 후 미수락 상태. 대시보드에서 수락 또는 거절 가능 |

---

## 팀원

| 이름 | 담당 영역 |
|---|---|
| 김은지 | 개인 대시보드 영역 |
| 김현수 | 팀 프로젝트 영역 |
| 배성민 | 팀 프로젝트 상세 영역 |
| 이재윤 | 공동 캘린더 영역 |
| 유관호 | 인증 및 회원가입, 로그인 영역 |
| 최민규 | 파일 업로드 영역 |
