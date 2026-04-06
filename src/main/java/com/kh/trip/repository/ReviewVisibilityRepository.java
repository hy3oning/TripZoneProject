package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.ReviewVisibility;

//리뷰 공개/숨김 상태 전용 Repository
public interface ReviewVisibilityRepository extends JpaRepository<ReviewVisibility, Long> {

	// 여러 리뷰 번호에 대한 공개 여부를 한 번에 조회
	List<ReviewVisibility> findByReviewNoIn(List<Long> reviewNos);
}
