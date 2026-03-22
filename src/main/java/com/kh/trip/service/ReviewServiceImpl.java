package com.kh.trip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Review;
import com.kh.trip.dto.ReviewCreateDTO;
import com.kh.trip.dto.ReviewSummaryDTO;
import com.kh.trip.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository; // 리뷰 저장/조회 repository

	@Override
	public ReviewSummaryDTO createReview(Long loginUserNo, ReviewCreateDTO reviewCreateDTO) {
		// 로그인 사용자 번호가 없으면 리뷰 작성 불가
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 작성할 수 있습니다.");
		}

		// 평점이 없거나 범위를 벗어나면 예외
		if (reviewCreateDTO.getRating() == null || reviewCreateDTO.getRating() < 1 || reviewCreateDTO.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1점부터 5점까지 가능합니다.");

		}

		// 리뷰 내용이 비어 있으면 예외
		if (reviewCreateDTO.getContent() == null || reviewCreateDTO.getContent().isBlank()) {
			throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
		}

		// 숙소 번호가 없으면 예외
		if (reviewCreateDTO.getLodgingNo() == null) {
			throw new IllegalArgumentException("숙소 번호는 필수입니다.");
		}

		// 예약 번호가 없으면 예외
		if (reviewCreateDTO.getBookingNo() == null) {
			throw new IllegalArgumentException("예약 번호는 필수입니다.");
		}

		// 리뷰 엔티티 생성
		Review review = Review.builder().bookingNo(reviewCreateDTO.getBookingNo()) // 예약 번호 세팅
				.userNo(loginUserNo) // 작성자는 로그인한 사용자 번호로 고정
				.lodgingNo(reviewCreateDTO.getLodgingNo()) // 숙소 번호 세팅
				.rating(reviewCreateDTO.getRating()) // 평점 세팅
				.content(reviewCreateDTO.getContent()) // 리뷰 내용 세팅
				.build(); // 엔티티 생성

		// DB에 저장
		Review savedReview = reviewRepository.save(review);

		return ReviewSummaryDTO.fromEntity(savedReview, List.of());
	}

}
