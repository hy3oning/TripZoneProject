package com.kh.trip.service;

import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.ReviewDTO;
import com.kh.trip.dto.ReviewStatsDTO;

public interface ReviewService {

	// 리뷰 작성
	ReviewDTO createReview(Long loginUserNo, ReviewDTO reviewDTO);

	// 리뷰 수정
	ReviewDTO updateReview(Long loginUserNo, Long reviewNo, ReviewDTO reviewDTO);

	// 리뷰 삭제
	void deleteReview(Long loginUserNo, Long reviewNo);

	// 페이징 처리 조회
	PageResponseDTO<ReviewDTO> getReviewsByLodging(Long lodgingNo, PageRequestDTO pageRequestDTO);

	// 숙소별 리뷰 통계 조회
	ReviewStatsDTO getReviewStatsByLodging(Long lodgingNo);

}
