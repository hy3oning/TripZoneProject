package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.Review;

// 리뷰 엔티티 전용 Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 숙소 목록 화면에서 리뷰 개수와 평균 평점을 요약 조회할 때 사용하는 Projection
	interface LodgingReviewSummary {
		Long getLodgingNo();
		Long getReviewCount();
		Double getReviewAverage();
	}

	// 특정 숙소의 리뷰 목록을 최신순으로 조회
	List<Review> findByLodging_LodgingNoOrderByReviewNoDesc(Long lodgingNo);

	// 같은 예약번호로 이미 리뷰가 존재하는지 확인
	boolean existsByBooking_BookingNo(Long bookingNo);

	// 리뷰 번호로 단건 조회
	Optional<Review> findByReviewNo(Long reviewNo);

	// 특정 숙소의 전체 리뷰 개수
	long countByLodging_LodgingNo(Long lodgingNo);

	// 특정 숙소의 특정 별점 개수
	long countByLodging_LodgingNoAndRating(Long lodgingNo, Integer rating);

	// 특정 숙소의 전체 리뷰 조회
	List<Review> findByLodging_LodgingNo(Long lodgingNo);

	// 특정 숙소의 리뷰를 페이징 조회
	Page<Review> findByLodging_LodgingNoOrderByReviewNoDesc(Long lodgingNo, Pageable pageable);

	// 숙소 목록 여러 개에 대해
	// 공개 가능한 리뷰만 기준으로 리뷰 수와 평균 평점을 한 번에 집계한다.
	@Query("""
			select r.lodging.lodgingNo as lodgingNo,
			       count(r) as reviewCount,
			       avg(r.rating) as reviewAverage
			from Review r
			left join ReviewVisibility rv on rv.reviewNo = r.reviewNo
			where r.lodging.lodgingNo in :lodgingNos
			  and (rv.reviewNo is null or rv.visible = true)
			group by r.lodging.lodgingNo
			""")
	List<LodgingReviewSummary> summarizeVisibleByLodgingNos(@Param("lodgingNos") List<Long> lodgingNos);
}