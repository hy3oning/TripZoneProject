# Backend Integration Notes

## 목적

프론트 `trip-zone`와 현재 백엔드 `TripZoneProject`를 실제 운영 화면 기준으로 다시 연결하면서, 백엔드에서 추가 보강한 지점을 정리한 문서다.

## 이번 보강 범위

### 1. 판매자 예약 상태 변경 API 추가

- 파일
  - `src/main/java/com/kh/trip/controller/SellerController.java`
  - `src/main/java/com/kh/trip/service/BookingService.java`
  - `src/main/java/com/kh/trip/service/BookingServiceImpl.java`

- 추가 내용
  - `PATCH /api/seller/bookings/{bookingNo}/status`
  - 요청 body
    - `{ "status": "CONFIRMED" | "COMPLETED" | "CANCELED" }`
  - 응답
    - 변경 후 `BookingDTO`

- 이유
  - 프론트 판매자 예약 관리 화면에서 `확정`, `완료`, `취소` 버튼이 있었지만 실제 호출할 백엔드 endpoint가 없어서 프론트에서 에러를 던지고 있었다.

- 내부 동작
  - `CONFIRMED`
    - `PENDING` 예약만 확정 가능
    - 이미 `CONFIRMED`면 현재 상태 그대로 반환
    - `CANCELED`, `COMPLETED`는 409
  - `COMPLETED`
    - 기존 `complete()` 재사용
  - `CANCELED`
    - 기존 `cancelBooking()` 재사용

## 프론트 연동 변경

### 1. 판매자 숙소 상태 변경 연결

- 프론트 파일
  - `trip-zone/frontend/src/services/dashboardService.js`
  - `trip-zone/frontend/src/pages/seller/SellerLodgingsPage.jsx`

- 설명
  - 기존에는 `updateSellerLodgingStatus()`가 에러를 던졌음
  - 현재는 `PATCH /api/lodgings/{lodgingId}`로 실제 상태 변경
  - `LodgingController`가 `@ModelAttribute` 기반이라 `FormData`로 `status`만 전송하도록 맞춤

### 2. 판매자 객실 상태 변경 연결

- 프론트 파일
  - `trip-zone/frontend/src/services/dashboardService.js`
  - `trip-zone/frontend/src/pages/seller/SellerRoomsPage.jsx`

- 설명
  - 기존에는 `updateSellerRoomStatus()`가 에러를 던졌음
  - 현재는 `PATCH /api/rooms/{roomId}`로 실제 상태 변경
  - 변경 후 응답으로 현재 행을 즉시 갱신

### 3. 판매자 예약 상태 변경 연결

- 프론트 파일
  - `trip-zone/frontend/src/services/dashboardService.js`
  - `trip-zone/frontend/src/pages/seller/SellerReservationsPage.jsx`

- 설명
  - 기존에는 `updateSellerReservationStatus()`가 에러를 던졌음
  - 현재는 `PATCH /api/seller/bookings/{bookingNo}/status` 호출
  - 변경 후 응답으로 현재 행 상태를 즉시 갱신

### 4. 마이페이지 문의 목록 API 정리

- 프론트 파일
  - `trip-zone/frontend/src/services/mypageService.js`

- 설명
  - 기존에는 예전 문의 API `/api/inquiry/list/{userNo}`를 직접 사용
  - 현재는 마이페이지 전용 endpoint `/api/mypage/inquiries`를 사용
  - 백엔드 `MypageDTO.InquiryResponse`의 `items` shape에 맞춰 매핑

### 5. 관리자 상태 변경 API 보강

- 백엔드 파일
  - `src/main/java/com/kh/trip/controller/AdminController.java`
  - `src/main/java/com/kh/trip/service/InquiryService.java`
  - `src/main/java/com/kh/trip/service/InquiryServiceImpl.java`

- 추가 내용
  - `PATCH /api/admin/users/{userNo}/status`
  - `PATCH /api/admin/inquiries/{inquiryNo}/status`

- 상태 매핑
  - 회원
    - `ACTIVE` -> `userService.restore(userNo)`
    - `BLOCKED` -> `userService.delete(userNo)`
  - 문의
    - `ANSWERED` -> `InquiryStatus.COMPLETED`
    - `CLOSED` -> `InquiryStatus.DELETE`

- 이유
  - 관리자 화면은 이미 상태 변경 버튼이 있었지만 실제 API가 없어 모두 에러를 던지고 있었다.
  - 회원 상태는 실제 백엔드 구현이 `enabled=1/0`만 가지므로 `DORMANT` 같은 가상 상태 대신 활성/차단 기준으로 정리했다.

### 6. 마이페이지 문의 상세 응답 전용화

- 백엔드 파일
  - `src/main/java/com/kh/trip/controller/MypageController.java`
  - `src/main/java/com/kh/trip/service/MypageService.java`
  - `src/main/java/com/kh/trip/service/MypageServiceImpl.java`
  - `src/main/java/com/kh/trip/dto/MypageDTO.java`
  - `src/main/java/com/kh/trip/repository/CommentRepository.java`

- 추가 내용
  - `GET /api/mypage/inquiries/{inquiryNo}`
  - 응답에 본문과 메시지 배열을 함께 포함하는 `InquiryDetailResponse` 추가

- 이유
  - 기존 프론트는 `/api/inquiry/{id}`와 `/api/comment/list`를 각각 호출해 클라이언트에서 억지로 합치고 있었다.
  - 마이페이지 전용 상세 응답으로 묶어야 프론트 상세/수정 화면이 같은 소스를 보게 된다.

### 7. 소셜 로그인 계정 연결 보강

- 백엔드 파일
  - `src/main/java/com/kh/trip/service/auth/AuthServiceImpl.java`

- 보강 내용
  - 소셜 로그인 시 같은 이메일의 기존 회원이 있으면 더 이상 즉시 실패시키지 않고 해당 회원에 소셜 provider를 연결
  - 소셜 신규 회원 생성 시에도 `BASIC` 등급을 명시적으로 부여

- 이유
  - 기존 구현은 소셜 로그인 사용자의 이메일이 로컬 계정 또는 다른 소셜 계정과 겹치면 `Email already exists`로 바로 실패했다.
  - 팀원 테스트 계정처럼 이미 가입된 이메일로 소셜 로그인할 때 반복적으로 auth 오류가 나는 원인이었다.

### 8. 카카오 로그인 redirect URI 전달 보강

- 백엔드 파일
  - `src/main/java/com/kh/trip/dto/auth/social/KakaoLoginRequestDTO.java`
  - `src/main/java/com/kh/trip/security/social/KakaoTokenVerifier.java`
  - `src/main/java/com/kh/trip/service/auth/AuthServiceImpl.java`

- 프론트 파일
  - `trip-zone/frontend/src/features/auth/authViewModels.js`

- 보강 내용
  - 프론트가 실제 카카오 인가 요청에 사용한 `redirectUri`를 `/api/auth/kakao` body에 함께 전달
  - 백엔드가 고정 설정값 대신 전달받은 `redirectUri`를 우선 사용해 토큰 교환
  - 카카오 토큰 교환 실패 시 원인 응답을 500 일반 예외로 삼키지 않고 메시지로 노출

- 이유
  - 프론트에서 받은 인가 코드의 redirect URI와 백엔드 토큰 교환 시 사용하는 redirect URI가 다르면 카카오가 `invalid_grant`로 거절한다.
  - 기존 구현은 이 실패를 모두 `Invalid Kakao code`라는 런타임 예외로 뭉개서 실제 원인 확인이 어려웠다.

### 9. 호스트 신청 제출 시각 DTO 보강

- 백엔드 파일
  - `src/main/java/com/kh/trip/dto/HostProfileDTO.java`
  - `src/main/java/com/kh/trip/service/HostProfileServiceImpl.java`

- 보강 내용
  - `HostProfileDTO`에 `regDate`, `updDate` 추가
  - 호스트 신청/재신청 이후 프론트가 실제 DB 저장 시각을 그대로 사용할 수 있게 응답 shape 확장

- 이유
  - 기존 프론트는 호스트 신청 초안 화면에서 마지막 제출 시각을 임시 현재 시각으로 채우고 있었다.
  - 제출 후 다시 불러온 상태를 실제 데이터 기준으로 보여주려면 DTO에 시간 필드가 필요했다.

### 10. 관리자 리뷰 숨김/복구 API 추가

- 백엔드 파일
  - `src/main/java/com/kh/trip/domain/ReviewVisibility.java`
  - `src/main/java/com/kh/trip/repository/ReviewVisibilityRepository.java`
  - `src/main/java/com/kh/trip/dto/ReviewAdminDTO.java`
  - `src/main/java/com/kh/trip/controller/ReviewController.java`
  - `src/main/java/com/kh/trip/service/ReviewService.java`
  - `src/main/java/com/kh/trip/service/ReviewServiceImpl.java`

- 추가 내용
  - `GET /api/reviews/admin`
  - `PATCH /api/reviews/{reviewNo}/visibility`
  - 공개 리뷰 목록과 숙소 리뷰 통계도 `REVIEW_VISIBILITY` 기준으로 필터링

- 이유
  - 기존 리뷰 운영 화면은 프론트 버튼만 있고 실제 숨김/복구 상태를 저장할 백엔드 구조가 없었다.
  - 기존 `REVIEWS` 테이블 컬럼을 건드리지 않기 위해 별도 가시성 테이블로 우회했다.

### 11. 판매자 자산 운영을 이미지 정렬 관리로 전환

- 프론트 파일
  - `trip-zone/frontend/src/services/dashboardService.js`
  - `trip-zone/frontend/src/pages/seller/SellerAssetsPage.jsx`

- 변경 내용
  - 기존 `노출/검수중` 가상 상태 버튼 제거
  - 실제 백엔드가 이미 지원하는 `uploadFileNames` 순서를 이용해
    - `대표 지정`
    - `뒤로 이동`
    로 바꿈
  - 숙소 수정 API `PATCH /api/lodgings/{lodgingNo}`에 재정렬된 `uploadFileNames`를 보내 실제 대표 이미지와 순서를 바꾼다.

- 이유
  - 이미지 검수 상태 컬럼은 백엔드에 없다.
  - 대신 실제로 존재하는 이미지 순서 모델을 사용하는 것이 데이터와 UI를 맞춘 방식이다.

### 12. 관리자 문의에 예약번호/숙소명 메타 저장 추가

- 백엔드 파일
  - `src/main/java/com/kh/trip/domain/Inquiry.java`
  - `src/main/java/com/kh/trip/dto/InquiryDTO.java`
  - `src/main/java/com/kh/trip/service/InquiryService.java`
  - `src/main/java/com/kh/trip/service/InquiryServiceImpl.java`
  - `src/main/java/com/kh/trip/service/MypageServiceImpl.java`

- 추가 내용
  - `INQUIRIES`에 `RELATED_BOOKING_NO`, `RELATED_LODGING_NAME` nullable 컬럼 추가
  - 문의 등록/수정 시 예약번호, 숙소명을 함께 저장
  - 마이페이지 문의 목록/상세 응답에서도 같은 메타 반환

- 이유
  - 기존 화면은 예약번호와 숙소명을 보여줄 자리가 있었지만 실제 저장은 제목/유형/내용만 하고 있었다.
  - 운영 문의 메타는 예약 FK가 아니라 참고 정보여서 문자열 보조 컬럼으로 두는 편이 변경 범위가 가장 작다.

### 13. 리뷰 이미지 업로드 API 추가

- 백엔드 파일
  - `src/main/java/com/kh/trip/controller/ReviewController.java`
  - `src/main/java/com/kh/trip/service/ReviewService.java`
  - `src/main/java/com/kh/trip/service/ReviewServiceImpl.java`

- 추가 내용
  - `POST /api/reviews/images`
  - multipart 업로드 파일을 기존 `CustomFileUtil`로 저장
  - 반환값은 저장된 파일명 배열
  - 프론트는 파일명을 실제 조회 가능한 URL로 바꿔 미리보기와 리뷰 등록 payload에 사용

- 이유
  - 리뷰 작성 폼은 사진 첨부 UI가 있었지만 실제로는 업로드가 연결되지 않아 공지 문구만 뜨고 있었다.
  - 기존 업로드 유틸과 `/api/view/**`를 재사용하면 새 스토리지 구조 없이 바로 붙일 수 있다.

## 검증 결과

### 프론트

- 명령
  - `npm run build`
- 결과
  - 성공

### 백엔드

- 명령
  - `mvn test -DskipTests=true`
- 결과
  - `BUILD SUCCESS`

## 현재 남아 있는 것

### 1. DB 한글 깨짐

- 숙소명/지역 등 일부 값이 `??`로 깨져 있으면 프론트-백엔드 연결과 별개로 DB 데이터 자체를 수정해야 한다.

### 2. 판매자/관리자 일부 액션 API

- 현재 연결한 것은 조회와 핵심 상태 변경 일부다.
- 아직 읽기 전용 또는 보강 대기인 영역
  - 리뷰 신고 집계
  - 판매자 자산 검수 상태

### 3. 마이페이지 문의 상세

- 목록과 상세는 이제 모두 `/api/mypage/inquiries`, `/api/mypage/inquiries/{id}` 기준으로 정리됐다.
- 다만 설계 문서의 `InquiryRoom`, `InquiryMessage` 구조와는 아직 다르다.
