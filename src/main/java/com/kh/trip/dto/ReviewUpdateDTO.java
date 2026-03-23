package com.kh.trip.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewUpdateDTO {

	private Integer rating; // 수정할 평점 값을 저장하는 필드
	private String content; // 수정할 평점 값을 저장하는 필드
	private List<String> imageUrls; // 수정할 리뷰 내용을 저장하는 필드
}