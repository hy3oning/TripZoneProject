package com.kh.trip.dto;

import com.kh.trip.domain.enums.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomUpdateDTO {

	private String roomName; // 수정할 객실 이름
	private String roomType; // 수정할 객실 타입
	private String roomDescription; // 수정할 설명
	private Integer maxGuestCount; // 수정할 최대 인원
	private Integer pricePerNight; // 수정할 1박 가격
	private Integer roomCount; // 수정할 객실 수
	private RoomStatus status; // 수정할 상태값
}
