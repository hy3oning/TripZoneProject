package com.kh.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // getter 자동 생성
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 전체 생성자
@Builder // builder 패턴
public class LodgingImageDTO {
	private Long imageNo; // 이미지 번호
	private String imageUrl; // 이미지 경로
	private Integer sortOrder; // 정렬 순서

}
