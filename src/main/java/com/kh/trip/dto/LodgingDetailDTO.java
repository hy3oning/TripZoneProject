package com.kh.trip.dto;

import java.util.List;


import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LodgingDetailDTO {

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

	private List<LodgingImageDTO> images; // 숙소 이미지 목록
	private List<RoomSummaryDTO> rooms; // 객실 목록

}
