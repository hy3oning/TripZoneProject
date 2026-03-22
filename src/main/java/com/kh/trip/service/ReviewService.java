package com.kh.trip.service;

import com.kh.trip.dto.ReviewCreateDTO;
import com.kh.trip.dto.ReviewSummaryDTO;

public interface ReviewService {

	// 로그인한 사용자 번호와 리뷰 작성 DTO를 받아서 리뷰를 등록
	public ReviewSummaryDTO createReview(Long loginUserNo, ReviewCreateDTO reviewCreateDTO);

}
