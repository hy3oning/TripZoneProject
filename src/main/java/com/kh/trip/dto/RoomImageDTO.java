package com.kh.trip.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImageDTO {

	private Long roomImageNo; // 객실 이미지 번호
	private String imageUrl; // 이미지 주소
	private Integer sortOrder; // 이미지 정렬 순서
	private LocalDateTime regDate; // 이미지 등록일

}
