# TripZone Integration Guide

## 목적
- 기준 DB: Oracle `FREEPDB1`
- 기준 프론트: `/Users/mudi/workspace/tripzone-final/frontend`
- 기준 백엔드: `/Users/mudi/workspace/TripZoneProject`
- 목표: mock 데이터를 단계적으로 제거하고, 팀원 백엔드 API를 실DB 기준으로 이어붙인다.

## 원칙
- 백엔드는 기존 기능을 갈아엎지 않는다.
- 필요한 범위만 `API 추가/보강` 중심으로 수정한다.
- 프론트는 응답 shape 흡수, 빈 상태 처리, 화면 연결을 담당한다.
- 백엔드 수정 파일은 반드시 아래 목록 기준으로 추적한다.
- 로컬 실행은 `backend/src/main/resources/application-local.properties` 기준으로 Oracle `FREEPDB1`에 붙인다.

## 현재 프론트 기준 실연동 완료 범위
- 공개/회원: 숙소 홈, 목록, 상세, 리뷰 조회
- 인증: 회원가입, 로그인
- 예약: 예약 생성
- 마이페이지: 예약, 찜, 쿠폰, 결제, 문의 조회
- 운영 일부: 관리자 회원/이벤트/문의 조회, 판매자 숙소/객실/예약/이미지/문의/신청 조회

## 현재 실제 수정된 백엔드 파일
- `/Users/mudi/workspace/tripzone-final/backend/src/main/resources/application-local.properties`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/dto/AdminUserStatusUpdateRequestDTO.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/repository/CommentRepository.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/dto/CommentDTO.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/CommentService.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/CommentServiceImpl.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/controller/CommentController.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/UserService.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/UserServiceImpl.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/controller/UserController.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/domain/Event.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/EventServiceImpl.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/dto/MileageHistoryDTO.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/repository/MileageHistoryRepository.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/MileageHistoryService.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/MileageHistoryServiceImpl.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/controller/MileageHistoryController.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/BookingService.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/service/BookingServiceImpl.java`
- `/Users/mudi/workspace/tripzone-final/backend/src/main/java/com/kh/trip/controller/BookingController.java`

적용 내용:
- 로컬 Oracle 전용 프로필 추가
- 관리자 회원 상태 변경 API 추가
- 문의별 댓글 조회 API 추가
- 댓글 응답 DTO에 등록일 추가
- 이벤트 수정 API가 상태/일정 변경까지 반영되도록 보강
- 마일리지 내역 조회 API 추가
- 판매자 예약 확정 API 추가

## 백엔드 우선 수정 파일

### 1. 회원/관리자 운영
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/UserController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/UserServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/UserDTO.java`

필요 작업:
- 관리자 회원 상태 변경 API 추가
- 회원 목록 응답에 권한/상태 표시용 필드 보강 필요 시 DTO 확장

### 2. 판매자 승인/관리
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/HostProfileController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/HostProfileServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/HostProfileDTO.java`

필요 작업:
- 승인/반려/중지 UI가 실제 동작하도록 상태 변경 응답 보강
- 판매자 본인 기준 조회 API 정리

### 3. 문의/댓글 스레드
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/InquiryController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/InquiryServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/InquiryDTO.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/CommentController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/CommentServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/CommentDTO.java`

필요 작업:
- 문의 상세별 댓글 목록 API
- 관리자/판매자 답변 등록 API
- 댓글 수정/삭제 응답 정리
- 문의 상태 전환 API 보강

### 4. 이벤트/쿠폰 운영
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/EventController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/EventServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/EventDTO.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/CouponController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/CouponServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/CouponDTO.java`

필요 작업:
- 이벤트 노출/숨김/수정 API 실사용 점검
- 쿠폰 대상/상태 응답 일관화

### 5. 리뷰 운영
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/ReviewController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/ReviewServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/ReviewSummaryDTO.java`

필요 작업:
- 관리자 리뷰 숨김/복구 API
- 신고 여부/운영 상태 필드 보강 필요

### 6. 판매자 숙소/객실/예약 운영
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/LodgingController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/LodgingServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/RoomController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/RoomServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/controller/BookingController.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/service/BookingServiceImpl.java`
- `/Users/mudi/workspace/TripZoneProject/src/main/java/com/kh/trip/dto/BookingDTO.java`

필요 작업:
- 판매자 기준 숙소 상태 변경 API
- 객실 예약 가능/불가 변경 API
- 판매자 기준 예약 상태 변경 API
- 판매자 전용 예약/숙소/객실 조회 응답 보강

## 프론트 우선 수정 파일

### 1. 공개/회원 공통
- `/Users/mudi/workspace/tripzone-final/frontend/src/lib/appClient.js`
- `/Users/mudi/workspace/tripzone-final/frontend/src/services/lodgingService.js`
- `/Users/mudi/workspace/tripzone-final/frontend/src/services/bookingService.js`
- `/Users/mudi/workspace/tripzone-final/frontend/src/services/mypageService.js`

### 2. 관리자/판매자 운영
- `/Users/mudi/workspace/tripzone-final/frontend/src/services/dashboardService.js`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminDashboardPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminUsersPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminSellersPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminEventsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminInquiriesPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminReviewsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminAuditLogsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerDashboardPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerApplyPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerLodgingsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerRoomsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerAssetsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerReservationsPage.jsx`
- `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerInquiriesPage.jsx`

필요 작업:
- 백엔드 응답 shape 흡수
- mock import 제거
- 쓰기 버튼은 실제 API 붙기 전까지 분기 처리
- 권한별 빈 상태/에러 처리

## 백엔드에서 아직 없거나 부족한 항목
- 관리자 회원 상태 변경 API
- 관리자 리뷰 운영 상태 변경 API
- 문의 댓글 스레드 조회/등록 API
- 판매자 기준 숙소/객실/예약 상태 변경 API
- 마일리지 내역 조회 API
- 운영 로그 조회 API

## 백엔드 완료 후 추가 연동 항목
- 관리자 리뷰 숨김/복구 저장 API 연결
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminReviewsPage.jsx`
  - 대상 서비스 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/services/dashboardService.js`
- 판매자 이미지 노출/검수 상태 저장 API 연결
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerAssetsPage.jsx`
  - 대상 서비스 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/services/dashboardService.js`
- 운영 로그 조회 API 연결
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/admin/AdminAuditLogsPage.jsx`
  - 대상 서비스 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/services/dashboardService.js`
- 문의 댓글 등록/수정/삭제 API 연결
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerInquiriesPage.jsx`
  - 대상 서비스 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/services/dashboardService.js`
- 판매자 숙소/객실 상태 변경 응답 보강 시 UI 상태 메시지 정리
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerLodgingsPage.jsx`
  - 대상 프론트 파일: `/Users/mudi/workspace/tripzone-final/frontend/src/pages/seller/SellerRoomsPage.jsx`

## 팀원 연결 기준
- 기준 DB는 네 Mac mini OrbStack Oracle이다.
- 접속 정보:
  - Host: `192.168.200.188`
  - Port: `1521`
  - Service name: `FREEPDB1`
  - Username: `app`
  - Password: `App123!`
- 백엔드 서버를 네가 띄워둘 경우, 팀원은 DB에 직접 붙는 대신 네 백엔드 API 주소를 프론트 환경값에 넣으면 된다.
- 팀원이 자기 로컬에서 백엔드를 직접 띄울 경우:
  - 공용 `application.properties`는 유지
  - 개인별 `application-local.properties` 또는 환경변수로 Oracle 접속 정보를 넣는다.
- 공통 기준 SQL은 아래 파일을 사용한다.
  - DDL: `/Users/mudi/workspace/tripzone-final/docs/tripzone-ddl-v2.sql`
  - Seed: `/Users/mudi/workspace/tripzone-final/docs/tripzone-seed-v1.sql`
- 팀원에게 전달할 최소 정보:
  - 백엔드 주소 또는 DB 주소 중 어떤 방식으로 붙을지
  - Oracle 서비스명 `FREEPDB1`
  - 계정 `app / App123!`
  - 현재 프론트는 `tripzone-final/frontend` 기준으로 연동 중이라는 점

## 작업 우선순위
1. 문의/댓글 API
2. 관리자 회원/판매자 상태 변경 API
3. 판매자 숙소/객실/예약 상태 변경 API
4. 리뷰 운영 API
5. 마일리지 내역/운영 로그 API

## 팀원 전달용 한 줄 기준
- DB와 프론트 실연동은 이미 진행 중
- 남은 건 대부분 운영용 API 보강
- 위 파일들 중심으로 `API 추가/보강`만 하면 프론트에서 나머지 연결 가능
