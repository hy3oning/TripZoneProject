package com.kh.trip.dto;

import java.util.List;

import com.kh.trip.domain.enums.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateDTO {

	private Long lodgingNo; // 어떤 숙소에 객실을 등록할지 숙소 번호 필요
	private String roomName; // 객실 이름
	private String roomType; // 객실 타입
	private String roomDescription; // 객실 설명
	private Integer maxGuestCount; // 최대 인원
	private Integer pricePerNight; // 1박 가격
	private Integer roomCount; // 객실 개수
	private RoomStatus status; // 상태값
	private List<String> imageUrls; // 객실 등록 시 함께 저장할 이미지 URL 목록

}
