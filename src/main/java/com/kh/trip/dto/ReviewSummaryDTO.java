package com.kh.trip.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSummaryDTO {

	private Long reviewNo; // 리뷰 번호
	private Long bookingNo; // 예약 번호
	private Long userNo; // 작성 회원 번호
	private Long lodgingNo;
	private Integer rating; // 평점
	private String content; // 리뷰 내용
	private LocalDateTime regDate; // 작성일
	private LocalDateTime updDate; // 수정일
	private List<ReviewImageDTO> images; // 리뷰 이미지 목록

}
