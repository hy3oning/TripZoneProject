package com.kh.trip.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.trip.domain.Booking;
import com.kh.trip.domain.Review;
import com.kh.trip.domain.ReviewImage;
import com.kh.trip.domain.ReviewVisibility;
import com.kh.trip.domain.enums.BookingStatus;
import com.kh.trip.dto.ReviewAdminDTO;
import com.kh.trip.dto.ReviewDTO;
import com.kh.trip.dto.ReviewStatsDTO;
import com.kh.trip.repository.BookingRepository;
import com.kh.trip.repository.ReviewImageRepository;
import com.kh.trip.repository.ReviewRepository;
import com.kh.trip.repository.ReviewVisibilityRepository;
import com.kh.trip.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

// ReviewService 인터페이스의 실제 구현체
// 리뷰 작성, 수정, 삭제, 목록 조회, 통계 조회, 관리자 숨김 처리 등을 담당한다.
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	// 리뷰 저장/조회 Repository
	private final ReviewRepository reviewRepository;

	// 예약 정보 검증용 Repository
	private final BookingRepository bookingRepository;

	// 리뷰 이미지 저장/조회/삭제 Repository
	private final ReviewImageRepository reviewImageRepository;

	// 리뷰 공개/숨김 상태 관리 Repository
	private final ReviewVisibilityRepository reviewVisibilityRepository;

	// 실제 파일 저장용 유틸
	private final CustomFileUtil fileUtil;

	// 리뷰 작성
	@Override
	public ReviewDTO createReview(Long loginUserNo, ReviewDTO reviewDTO) {

		// 1. 로그인 사용자 검증
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 작성할 수 있습니다.");
		}

		// 2. 평점 검증
		if (reviewDTO.getRating() == null || reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1점부터 5점까지 가능합니다.");
		}

		// 3. 리뷰 내용 검증
		if (reviewDTO.getContent() == null || reviewDTO.getContent().isBlank()) {
			throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
		}

		// 4. 숙소 번호 검증
		if (reviewDTO.getLodgingNo() == null) {
			throw new IllegalArgumentException("숙소 번호는 필수입니다.");
		}

		// 5. 예약 번호 검증
		if (reviewDTO.getBookingNo() == null) {
			throw new IllegalArgumentException("예약 번호는 필수입니다.");
		}

		// 6. 같은 예약번호로 리뷰가 이미 존재하는지 확인
		// 예약 1건당 리뷰 1개만 허용하기 위한 로직
		if (reviewRepository.existsByBooking_BookingNo(reviewDTO.getBookingNo())) {
			throw new IllegalArgumentException("이미 해당 예약에 대한 리뷰가 존재합니다.");
		}

		// 7. 예약 정보 조회
		Booking booking = bookingRepository.findDetailByBookingNo(reviewDTO.getBookingNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

		// 8. 본인 예약인지 검증
		if (!booking.getUser().getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인의 예약에 대해서만 리뷰를 작성할 수 있습니다.");
		}

		// 9. 예약한 숙소와 요청한 숙소 번호가 일치하는지 검증
		if (reviewDTO.getLodgingNo() != null
				&& !booking.getRoom().getLodging().getLodgingNo().equals(reviewDTO.getLodgingNo())) {
			throw new IllegalArgumentException("예약한 숙소에 대해서만 리뷰를 작성할 수 있습니다.");
		}

		// 10. 투숙 완료 상태(COMPLETED)인지 확인
		if (booking.getStatus() != BookingStatus.COMPLETED) {
			throw new IllegalArgumentException("투숙 완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
		}

		// 11. Review 엔티티 생성
		Review review = Review.builder()
				.booking(booking)
				.user(booking.getUser())
				.lodging(booking.getRoom().getLodging())
				.rating(reviewDTO.getRating())
				.content(reviewDTO.getContent().trim())
				.build();

		// 12. 리뷰 저장
		Review savedReview = reviewRepository.save(review);

		// 13. 리뷰 이미지 저장
		saveReviewImages(savedReview, reviewDTO.getImageUrls());

		// 14. 저장 후 이미지 목록 다시 조회
		List<String> imageUrls = reviewImageRepository
				.findByReview_ReviewNoOrderBySortOrderAsc(savedReview.getReviewNo()).stream()
				.map(ReviewImage::getImageUrl)
				.toList();

		return toReviewDTO(savedReview, imageUrls);
	}

	// 리뷰 수정
	@Override
	public ReviewDTO updateReview(Long loginUserNo, Long reviewNo, ReviewDTO reviewDTO) {

		// 1. 로그인 사용자 검증
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 수정할 수 있습니다.");
		}

		// 2. 리뷰 번호 검증
		if (reviewNo == null) {
			throw new IllegalArgumentException("리뷰 번호는 필수입니다.");
		}

		// 3. 수정 DTO 검증
		if (reviewDTO == null) {
			throw new IllegalArgumentException("수정할 리뷰 정보가 없습니다.");
		}

		// 4. 평점 검증
		if (reviewDTO.getRating() == null || reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1점부터 5점까지 가능합니다.");
		}

		// 5. 내용 검증
		if (reviewDTO.getContent() == null || reviewDTO.getContent().isBlank()) {
			throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
		}

		// 6. 기존 리뷰 조회
		Review review = reviewRepository.findByReviewNo(reviewNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		// 7. 본인 리뷰만 수정 가능
		if (!review.getUser().getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
		}

		// 8. 평점 수정
		if (reviewDTO.getRating() != null) {
			if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
				throw new IllegalArgumentException("평점은 1점부터 5점까지 가능합니다.");
			}
			review.changeRating(reviewDTO.getRating());
		}

		// 9. 내용 수정
		if (reviewDTO.getContent() != null) {
			if (reviewDTO.getContent().isBlank()) {
				throw new IllegalArgumentException("리뷰 내용은 비워둘 수 없습니다.");
			}
			review.changeContent(reviewDTO.getContent().trim());
		}

		// 10. 수정 반영 저장
		Review updatedReview = reviewRepository.save(review);

		// 11. 이미지가 새로 들어오면 기존 이미지 전체 삭제 후 다시 저장
		if (reviewDTO.getImageUrls() != null) {
			reviewImageRepository.deleteByReview_ReviewNo(reviewNo);
			saveReviewImages(updatedReview, reviewDTO.getImageUrls());
		}

		// 12. 수정 후 이미지 목록 다시 조회
		List<String> imageUrls = reviewImageRepository.findByReview_ReviewNoOrderBySortOrderAsc(reviewNo).stream()
				.map(ReviewImage::getImageUrl)
				.toList();

		return toReviewDTO(updatedReview, imageUrls);
	}

	// 리뷰 삭제
	@Override
	public void deleteReview(Long loginUserNo, Long reviewNo) {

		// 1. 로그인 사용자 검증
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 삭제할 수 있습니다.");
		}

		// 2. 리뷰 번호 검증
		if (reviewNo == null) {
			throw new IllegalArgumentException("리뷰 번호는 필수입니다.");
		}

		// 3. 리뷰 조회
		Review review = reviewRepository.findByReviewNo(reviewNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		// 4. 본인 리뷰만 삭제 가능
		if (!review.getUser().getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
		}

		// 5. FK 충돌 방지를 위해 REVIEW_IMAGES부터 먼저 삭제
		reviewImageRepository.deleteByReview_ReviewNo(reviewNo);

		// 6. 리뷰 본문 삭제
		reviewRepository.delete(review);
	}

	// 숙소별 리뷰 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<ReviewDTO> getReviewsByLodging(Long lodgingNo) {

		if (lodgingNo == null) {
			throw new IllegalArgumentException("숙소 번호는 필수입니다.");
		}

		// 공개 가능한 리뷰만 필터링
		List<Review> visibleReviews =
				filterVisibleReviews(reviewRepository.findByLodging_LodgingNoOrderByReviewNoDesc(lodgingNo));

		// 이미지 목록을 포함해서 DTO 변환
		return visibleReviews.stream().map(review -> {
			List<String> imageUrls = reviewImageRepository
					.findByReview_ReviewNoOrderBySortOrderAsc(review.getReviewNo()).stream()
					.map(ReviewImage::getImageUrl)
					.toList();

			return toReviewDTO(review, imageUrls);
		}).toList();
	}

	// 숙소별 리뷰 통계 조회
	@Override
	@Transactional(readOnly = true)
	public ReviewStatsDTO getReviewStatsByLodging(Long lodgingNo) {

		// 숙소 번호 검증
		if (lodgingNo == null) {
			throw new IllegalArgumentException("숙소 번호는 필수입니다.");
		}

		// 공개 가능한 리뷰만 통계 대상
		List<Review> reviews = filterVisibleReviews(reviewRepository.findByLodging_LodgingNo(lodgingNo));

		// 전체 리뷰 수
		long totalReviewCount = reviews.size();

		// 별점별 개수 계산
		long rating5Count = reviews.stream().filter(review -> review.getRating() == 5).count();
		long rating4Count = reviews.stream().filter(review -> review.getRating() == 4).count();
		long rating3Count = reviews.stream().filter(review -> review.getRating() == 3).count();
		long rating2Count = reviews.stream().filter(review -> review.getRating() == 2).count();
		long rating1Count = reviews.stream().filter(review -> review.getRating() == 1).count();

		// 평균 평점 계산
		double averageRating = 0.0;

		if (totalReviewCount > 0) {
			double sum = reviews.stream().mapToInt(Review::getRating).sum();
			averageRating = sum / totalReviewCount;

			// 소수점 첫째 자리까지 반올림
			averageRating = BigDecimal.valueOf(averageRating)
					.setScale(1, RoundingMode.HALF_UP)
					.doubleValue();
		}

		return toReviewStatsDTO(
				totalReviewCount,
				averageRating,
				rating5Count,
				rating4Count,
				rating3Count,
				rating2Count,
				rating1Count
		);
	}

	// 관리자용 리뷰 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<ReviewAdminDTO> getAdminReviews() {

		// 전체 리뷰를 최신순으로 정렬
		List<Review> reviews = reviewRepository.findAll().stream()
				.sorted((left, right) -> right.getReviewNo().compareTo(left.getReviewNo()))
				.toList();

		// 리뷰 공개/숨김 상태 맵 조회
		Map<Long, Boolean> visibilityMap = loadVisibilityMap(reviews);

		return reviews.stream()
				.map(review -> toReviewAdminDTO(review, visibilityMap.getOrDefault(review.getReviewNo(), true)))
				.toList();
	}

	// 관리자용 공개/숨김 상태 변경
	@Override
	public ReviewAdminDTO updateReviewVisibility(Long reviewNo, String status) {

		// 리뷰 존재 여부 확인
		Review review = reviewRepository.findByReviewNo(reviewNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		// 문자열 상태를 boolean 값으로 변환
		boolean visible;
		if ("VISIBLE".equals(status)) {
			visible = true;
		} else if ("HIDDEN".equals(status)) {
			visible = false;
		} else {
			throw new IllegalArgumentException("지원하지 않는 리뷰 상태입니다. status=" + status);
		}

		// 기존 공개 여부 정보가 있으면 수정, 없으면 새로 생성
		ReviewVisibility reviewVisibility = reviewVisibilityRepository.findById(reviewNo)
				.orElse(ReviewVisibility.builder().reviewNo(reviewNo).build());

		reviewVisibility.changeVisible(visible);
		reviewVisibilityRepository.save(reviewVisibility);

		return toReviewAdminDTO(review, visible);
	}

	// 리뷰 이미지 파일 업로드
	@Override
	public List<String> uploadReviewImages(List<MultipartFile> files) {

		if (files == null || files.isEmpty()) {
			return List.of();
		}

		// null 이거나 비어 있는 파일은 제거
		List<MultipartFile> validFiles = files.stream()
				.filter(file -> file != null && !file.isEmpty())
				.toList();

		if (validFiles.isEmpty()) {
			return List.of();
		}

		return fileUtil.saveFiles(validFiles);
	}

	// Review 엔티티 + 이미지 목록 -> ReviewDTO 변환
	private ReviewDTO toReviewDTO(Review review, List<String> imageUrls) {
		return ReviewDTO.builder()
				.reviewNo(review.getReviewNo())
				.bookingNo(review.getBooking().getBookingNo())
				.userName(review.getUser().getUserName())
				.userNo(review.getUser().getUserNo())
				.lodgingNo(review.getLodging().getLodgingNo())
				.rating(review.getRating())
				.content(review.getContent())
				.regDate(review.getRegDate())
				.updDate(review.getUpdDate())
				.imageUrls(imageUrls)
				.build();
	}

	// 리뷰 이미지 URL 목록 저장 공통 메서드
	private void saveReviewImages(Review review, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		// index + 1 값을 sortOrder로 사용
		IntStream.range(0, imageUrls.size())
				.mapToObj(index -> ReviewImage.builder()
						.review(review)
						.imageUrl(imageUrls.get(index))
						.sortOrder(index + 1)
						.build())
				.forEach(reviewImageRepository::save);
	}

	// 통계 결과를 DTO로 변환
	private ReviewStatsDTO toReviewStatsDTO(long totalCount, double avgRating, long r5, long r4, long r3, long r2, long r1) {
		return ReviewStatsDTO.builder()
				.totalReviewCount(totalCount)
				.averageRating(avgRating)
				.rating5Count(r5)
				.rating4Count(r4)
				.rating3Count(r3)
				.rating2Count(r2)
				.rating1Count(r1)
				.build();
	}

	// 공개 가능한 리뷰만 걸러내는 공통 메서드
	private List<Review> filterVisibleReviews(List<Review> reviews) {
		Map<Long, Boolean> visibilityMap = loadVisibilityMap(reviews);
		return reviews.stream()
				.filter(review -> visibilityMap.getOrDefault(review.getReviewNo(), true))
				.toList();
	}

	// 리뷰 번호별 공개 여부 맵 생성
	private Map<Long, Boolean> loadVisibilityMap(List<Review> reviews) {
		Map<Long, Boolean> visibilityMap = new HashMap<>();

		if (reviews.isEmpty()) {
			return visibilityMap;
		}

		List<Long> reviewNos = reviews.stream().map(Review::getReviewNo).toList();

		reviewVisibilityRepository.findByReviewNoIn(reviewNos)
				.forEach(item -> visibilityMap.put(item.getReviewNo(), item.isVisible()));

		return visibilityMap;
	}

	// 관리자용 DTO 변환
	private ReviewAdminDTO toReviewAdminDTO(Review review, boolean visible) {
		return ReviewAdminDTO.builder()
				.reviewNo(review.getReviewNo())
				.lodgingNo(review.getLodging().getLodgingNo())
				.lodgingName(review.getLodging().getLodgingName())
				.userNo(review.getUser().getUserNo())
				.userName(review.getUser().getUserName())
				.rating(review.getRating())
				.content(review.getContent())
				.status(visible ? "VISIBLE" : "HIDDEN")
				.regDate(review.getRegDate())
				.build();
	}
}