# TripZoneProject

Spring Boot 기반 숙소 예약 플랫폼 백엔드 프로젝트입니다.  
사용자, 호스트, 관리자 역할을 기준으로 숙소 조회, 예약, 결제, 리뷰, 문의, 쿠폰, 마이페이지, 판매자 관리 기능을 구현했습니다.

## Overview

TripZoneProject는 여행 숙소 예약 서비스를 주제로 한 백엔드 학습 프로젝트입니다.  
실제 서비스와 유사한 흐름을 기준으로 도메인을 나누고, 계층형 아키텍처를 적용해 기능을 구현했습니다.

이 프로젝트를 통해 다음 내용을 중점적으로 학습했습니다.

- Spring Boot 기반 REST API 설계
- JPA 엔티티 연관관계 매핑
- Spring Security + JWT 인증/인가
- 예약/결제/리뷰/문의 등 상태 기반 비즈니스 로직 처리
- 역할별 기능 분리
- 프론트엔드 연동을 고려한 DTO 설계

## Tech Stack

- Java 17
- Spring Boot 3.5.11
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring WebSocket
- Oracle Database
- JWT
- Lombok
- ModelMapper
- Thumbnailator
- JSP / JSTL
- Maven

## Main Features

### User

- 회원가입 및 로그인
- JWT 기반 인증
- 숙소 목록/상세 조회
- 위시리스트 관리
- 예약 생성 및 예약 조회
- 결제 처리
- 쿠폰 및 마일리지 기능
- 리뷰 작성 및 리뷰 이미지 업로드
- 마이페이지 기능

### Host

- 호스트 프로필 관리
- 숙소 및 객실 등록/수정/상태 관리
- 본인 숙소 예약 목록 조회
- 예약 상태 변경
- 판매자 대시보드 확장 구조

### Admin

- 사용자 상태 관리
- 문의 상태 관리
- 리뷰 노출 여부 관리

## Project Structure

```text
src/main/java/com/kh/trip
├─ config
├─ controller
├─ domain
├─ dto
├─ repository
├─ security
├─ service
└─ util
```

### Layered Architecture

- `controller` : API 요청/응답 처리
- `service` : 비즈니스 로직 처리
- `repository` : 데이터 조회 및 저장
- `domain` : 엔티티 및 도메인 상태 관리
- `dto` : 요청/응답 데이터 분리
- `security` : 인증, 인가, JWT 처리

## Core Domains

- `User`
- `HostProfile`
- `Lodging`
- `Room`
- `Booking`
- `Payment`
- `Coupon`
- `UserCoupon`
- `Review`
- `Inquiry`
- `WishList`
- `MileageHistory`

## Example APIs

### Booking / Seller

- `GET /api/seller/bookings`
- `PATCH /api/seller/bookings/{bookingNo}/status`

### Lodging / Room

- `GET /api/lodgings`
- `PATCH /api/lodgings/{lodgingNo}`
- `PATCH /api/rooms/{roomNo}`

### Review

- `POST /api/reviews/images`
- `GET /api/reviews/admin`
- `PATCH /api/reviews/{reviewNo}/visibility`

### Inquiry / Mypage

- `POST /api/inquiry`
- `GET /api/mypage/inquiries`
- `GET /api/mypage/inquiries/{inquiryNo}`

### Admin

- `PATCH /api/admin/users/{userNo}/status`
- `PATCH /api/admin/inquiries/{inquiryNo}/status`

## What I Learned

- 엔티티 연관관계를 실제 서비스 흐름에 맞게 설계하는 방법
- 예약과 결제처럼 상태 전이가 있는 도메인을 서비스 계층에서 관리하는 방법
- Controller, Service, Repository 책임을 분리하는 구조
- 프론트엔드에서 사용하기 좋은 응답 형태를 DTO로 설계하는 방법
- 인증/인가를 역할별 기능에 연결하는 방법
- 파일 업로드와 이미지 처리 기능 구현 방식

## Run

### Requirements

- Java 17
- Oracle Database
- Maven Wrapper 실행 가능 환경

### Build

```bash
./mvnw.cmd clean compile
```

### Run Application

```bash
./mvnw.cmd spring-boot:run
```

## Configuration

설정 파일은 아래 경로를 기준으로 관리합니다.

- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties.example`

로컬 환경에서는 DB 계정, JWT 시크릿, OAuth 키 같은 민감한 설정을 별도 파일이나 환경 변수로 분리하는 것을 권장합니다.

## Improvements

- 테스트 코드 보강
- 통계 API 확장
- 공통 예외 처리 정리
- Swagger/OpenAPI 문서화
- 운영/배포 환경 구성

---

학습용 프로젝트이지만, 실제 서비스 구조를 기준으로 예약 플랫폼 백엔드의 핵심 흐름을 경험하는 데 초점을 맞춰 구현했습니다.
