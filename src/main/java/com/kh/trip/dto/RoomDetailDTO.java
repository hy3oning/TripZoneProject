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
public class RoomDetailDTO {
	
	private Long roomNo; // 객실 번호
    private Long lodgingNo; // 어떤 숙소에 속한 객실인지 보여주기 위해 추가
    private String roomName; // 객실 이름
    private String roomType; // 객실 타입
    private String roomDescription; // 객실 설명
    private Integer maxGuestCount; // 최대 수용 인원
    private Integer pricePerNight; // 1박 가격
    private Integer roomCount; // 객실 개수
    private RoomStatus status; // 객실 상태
    private List<RoomImageDTO> images; // 객실 상세 조회 시 이미지 목록 포함

}
