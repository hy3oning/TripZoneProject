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
public class RoomUpdateDTO {
	
	private Long lodgingNo; // 객실이 속한 숙소 자체를 변경할 때 사용할 숙소 번호
	private String roomName; // 수정할 객실 이름
	private String roomType; // 수정할 객실 타입
	private String roomDescription; // 수정할 설명
	private Integer maxGuestCount; // 수정할 최대 인원
	private Integer pricePerNight; // 수정할 1박 가격
	private Integer roomCount; // 수정할 객실 수
	private RoomStatus status; // 수정할 상태값
	private List<String> imageUrls; // 수정 시 새로 저장할 이미지 URL 목록
}
