package com.kh.trip.service;

import com.kh.trip.dto.ReviewCreateDTO;
import com.kh.trip.dto.ReviewSummaryDTO;
import com.kh.trip.dto.ReviewUpdateDTO;

public interface ReviewService {

	// 리뷰 작성
	public ReviewSummaryDTO createReview(Long loginUserNo, ReviewCreateDTO reviewCreateDTO);

	// 리뷰 수정
	public ReviewSummaryDTO updateReview(Long loginUserNo, Long reviewNo, ReviewUpdateDTO reviewUpdateDTO);

	// 리뷰 삭제
	public void deleteReview(Long loginUserNo, Long reviewNo);

}
