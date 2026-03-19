package com.kh.trip.dto;

import com.kh.trip.domain.LodgingImage;

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

	// Entity -> DTO 변환 메서드
	public static LodgingImageDTO fromEntity(LodgingImage lodgingImage) {
		return LodgingImageDTO.builder().imageNo(lodgingImage.getImageNo()) // 이미지 번호 세팅
				.imageUrl(lodgingImage.getImageUrl()) // 이미지 경로 세팅
				.sortOrder(lodgingImage.getSortOrder()) // 정렬 순서 세팅
				.build(); // DTO 생성
	}

}
