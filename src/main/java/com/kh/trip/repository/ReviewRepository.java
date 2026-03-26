package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kh.trip.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 특정 숙소 번호의 리뷰 목록을 최신순(리뷰번호 내림차순)으로 조회
	List<Review> findByLodgingNoOrderByReviewNoDesc(Long lodgingNo);

	// 같은 예약번호로 이미 리뷰가 있는지 확인
	boolean existsByBooking_BookingNo(Long bookingNo);

	// 리뷰 번호로 단건 조회
	Optional<Review> findByReviewNo(Long reviewNo);

	// 특정 숙소의 전체 리뷰 개수
	long countByLodgingNo(Long lodgingNo);

	// 특정 숙소의 특정 별점 개수
	long countByLodgingNoAndRating(Long lodgingNo, Integer rating);

	// 특정 숙소의 리뷰 전체 조회 (평균 계산용)
	List<Review> findByLodgingNo(Long lodgingNo);
}
