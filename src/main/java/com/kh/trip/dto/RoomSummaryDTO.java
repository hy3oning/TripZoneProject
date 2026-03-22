package com.kh.trip.dto;

import com.kh.trip.domain.Room;
import com.kh.trip.domain.enums.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
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

	// Entity -> DTO 변환 
	public static RoomSummaryDTO fromEntity(Room room) {
		return RoomSummaryDTO.builder().roomNo(room.getRoomNo()) // 객실 번호
				.roomName(room.getRoomName()) // 객실명
				.roomType(room.getRoomType()) // 객실 유형
				.roomDescription(room.getRoomDescription()) // 설명
				.maxGuestCount(room.getMaxGuestCount()) // 최대 인원
				.pricePerNight(room.getPricePerNight()) // 가격
				.roomCount(room.getRoomCount()) // 객실 수
				.status(room.getStatus()) // 상태
				.build(); // DTO 생성
	}
	
	// DTO -> Entity 변환
	public static Room toEntity(RoomSummaryDTO roomDTO) {
		return Room.builder().roomNo(roomDTO.getRoomNo()) // 객실 번호
				.roomName(roomDTO.getRoomName()) // 객실명
				.roomType(roomDTO.getRoomType()) // 객실 유형
				.roomDescription(roomDTO.getRoomDescription()) // 설명
				.maxGuestCount(roomDTO.getMaxGuestCount()) // 최대 인원
				.pricePerNight(roomDTO.getPricePerNight()) // 가격
				.roomCount(roomDTO.getRoomCount()) // 객실 수
				.status(roomDTO.getStatus()) // 상태
				.build(); // DTO 생성
	}
}
