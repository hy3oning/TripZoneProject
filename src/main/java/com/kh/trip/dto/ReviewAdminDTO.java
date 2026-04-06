package com.kh.trip.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 관리자 화면에서 리뷰를 조회/관리하기 위한 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAdminDTO {

	private Long reviewNo; // 리뷰 번호
	private Long lodgingNo; // 숙소 번호
	private String lodgingName; // 숙소명
	private Long userNo; // 작성자 회원 번호
	private String userName; // 작성자 이름
	private Integer rating; // 평점
	private String content; // 리뷰 내용
	
	// 리뷰 상태
	// VISIBLE 또는 HIDDEN 형태로 사용
	private String status;
	private LocalDateTime regDate; // 작성일
}