package com.kh.trip.dto;

import java.util.List;

import com.kh.trip.domain.enums.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSummaryDTO {

	private Long roomNo; // 객실 번호
	private String roomName; // 객실명
	private String roomType; // 객실 유형
	private String roomDescription; // 객실 설명
	private Integer maxGuestCount; // 최대 인원
	private Integer pricePerNight; // 1박 가격
	private Integer roomCount; // 객실 수
	private RoomStatus status; // 객실 상태
	private List<RoomImageDTO> images; // 객실 목록 조회 시 이미지 목록 포함

}
