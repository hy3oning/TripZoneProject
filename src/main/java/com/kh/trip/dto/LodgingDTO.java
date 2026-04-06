package com.kh.trip.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LodgingDTO {

	private Long lodgingNo; // 숙소 번호
	private Long hostNo; // 호스트 번호
	private String lodgingName; // 숙소명
	private LodgingType lodgingType; // 숙소 유형
	private String region; // 지역
	private String address; // 기본 주소
	private String detailAddress; // 상세 주소
	private String zipCode; // 우편번호
	private Double latitude; // 위도
	private Double longitude; // 경도
	private String description; // 숙소 설명
	private String checkInTime; // 체크인 시간
	private String checkOutTime; // 체크아웃 시간
	private LodgingStatus status; // 숙소 상태
	private Double reviewAverage; // 리뷰 평균 평점
	private Long reviewCount; // 공개 리뷰 수

	// 새로 업로드할 파일 목록
	// 등록/수정 요청 시 MultipartFile 형태로 전달받는다.
	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();

	// 실제 서버에 저장된 파일명 목록
	// DB 저장 시 LodgingImage와 연결되거나,
	// 조회 응답 시 프론트에 이미지 목록으로 내려줄 때 사용한다.
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();

	// 숙소 상세조회 시 함께 내려줄 객실 목록
	// 상세 페이지나 판매자 관리 화면에서 사용된다.
	@Builder.Default
	private List<RoomDTO> rooms = new ArrayList<>();

	// 프론트나 외부 입력에서 postCode라는 이름으로 들어와도
	// 내부적으로는 zipCode 필드에 매핑되도록 만든 보조 setter
	public void setPostCode(String postCode) {
		this.zipCode = postCode;
	}

	// 프론트나 외부 입력에서 checkIn이라는 이름으로 들어온 값을
	// checkInTime 필드에 매핑하기 위한 보조 setter
	public void setCheckIn(String checkIn) {
		this.checkInTime = checkIn;
	}

	// 프론트나 외부 입력에서 checkOut이라는 이름으로 들어온 값을
	// checkOutTime 필드에 매핑하기 위한 보조 setter
	public void setCheckOut(String checkOut) {
		this.checkOutTime = checkOut;
	}
}
