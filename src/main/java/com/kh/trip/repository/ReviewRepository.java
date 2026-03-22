package com.kh.trip.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kh.trip.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 특정 숙소 번호의 리뷰 목록을 최신순(리뷰번호 내림차순)으로 조회
	List<Review> findByLodgingNoOrderByReviewNoDesc(Long lodgingNo);

	// 같은 예약번호로 이미 리뷰가 있는지 확인
	boolean existsByBookingNo(Long bookingNo);
}
