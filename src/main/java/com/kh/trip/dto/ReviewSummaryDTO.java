package com.kh.trip.dto;

import java.util.List;
import com.kh.trip.domain.Review;
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
	private Integer rating; // 평점
	private String content; // 리뷰 내용
	private List<ReviewImageDTO> images; // 리뷰 이미지 목록

	// Review 엔티티 + 리뷰 이미지 DTO 목록을 합쳐서 DTO로 변환
	public static ReviewSummaryDTO fromEntity(Review review, List<ReviewImageDTO> images) {
		return ReviewSummaryDTO.builder().reviewNo(review.getReviewNo()) // 리뷰 번호 세팅
				.bookingNo(review.getBookingNo()) // 예약 번호 세팅
				.userNo(review.getUserNo()) // 작성 회원 번호 세팅
				.rating(review.getRating()) // 평점 세팅
				.content(review.getContent()) // 리뷰 내용 세팅
				.images(images) // 리뷰 이미지 목록 세팅
				.build(); // DTO 생성
	}
}
