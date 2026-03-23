package com.kh.trip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Booking;
import com.kh.trip.domain.Review;
import com.kh.trip.domain.enums.BookingStatus;
import com.kh.trip.dto.ReviewCreateDTO;
import com.kh.trip.dto.ReviewSummaryDTO;
import com.kh.trip.dto.ReviewUpdateDTO;
import com.kh.trip.repository.BookingRepository;
import com.kh.trip.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository; // 리뷰 저장/조회 repository
	private final BookingRepository bookingRepository; // 예약 정보 검증용 repository

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

		// 같은 예약번호로 이미 리뷰가 있는지 확인
		// 예약당 리뷰 1개만 허용하려면 반드시 필요
		if (reviewRepository.existsByBookingNo(reviewCreateDTO.getBookingNo())) {
			throw new IllegalArgumentException("이미 해당 예약에 대한 리뷰가 존재합니다.");
		}

		// 예약 정보 조회
		// 이 예약이 실제 존재하는지, 누구 예약인지, 어떤 숙소 예약인지 확인하기 위해 필요
		Booking booking = bookingRepository.findDetailByBookingNo(reviewCreateDTO.getBookingNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

		// 본인 예약인지 확인
		if (!booking.getUser().getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인의 예약에 대해서만 리뷰를 작성할 수 있습니다.");
		}

		// 예약한 숙소와 요청한 숙소가 같은지 확인
		if (!booking.getRoom().getLodgingNo().equals(reviewCreateDTO.getLodgingNo())) {
			throw new IllegalArgumentException("예약한 숙소에 대해서만 리뷰를 작성할 수 있습니다.");
		}

		// 투숙 완료 상태(COMPLETED)인지 확인
		if (booking.getStatus() != BookingStatus.COMPLETED) {
			throw new IllegalArgumentException("투숙 완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
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

	@Override
	public ReviewSummaryDTO updateReview(Long loginUserNo, Long reviewNo, ReviewUpdateDTO reviewUpdateDTO) {
		// 로그인 사용자 번호가 없으면 리뷰 수정 불가
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 수정할 수 있습니다.");
		}

		// 리뷰 번호가 없으면 예외
		if (reviewNo == null) {
			throw new IllegalArgumentException("리뷰 번호는 필수입니다.");
		}

		// 수정 DTO가 없으면 예외
		if (reviewUpdateDTO == null) {
			throw new IllegalArgumentException("수정할 리뷰 정보가 없습니다.");
		}

		// 평점이 없거나 범위를 벗어나면 예외
		if (reviewUpdateDTO.getRating() == null || reviewUpdateDTO.getRating() < 1 || reviewUpdateDTO.getRating() > 5) {
			throw new IllegalArgumentException("평점은 1점부터 5점까지 가능합니다.");
		}

		// 리뷰 내용이 비어 있으면 예외
		if (reviewUpdateDTO.getContent() == null || reviewUpdateDTO.getContent().isBlank()) {
			throw new IllegalArgumentException("리뷰 내용은 필수입니다.");
		}

		// 리뷰 조회
		Review review = reviewRepository.findByReviewNo(reviewNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		// 본인 리뷰만 수정 가능
		if (!review.getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
		}

		// 수정
		review.setRating(reviewUpdateDTO.getRating());
		review.setContent(reviewUpdateDTO.getContent().trim());

		Review updatedReview = reviewRepository.save(review);

		return ReviewSummaryDTO.fromEntity(updatedReview, List.of());
	}

	@Override
	public void deleteReview(Long loginUserNo, Long reviewNo) {
		// 로그인 사용자 번호가 없으면 리뷰 삭제 불가
		if (loginUserNo == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 삭제할 수 있습니다.");
		}

		// 리뷰 번호가 없으면 예외
		if (reviewNo == null) {
			throw new IllegalArgumentException("리뷰 번호는 필수입니다.");
		}

		// 리뷰 조회
		Review review = reviewRepository.findByReviewNo(reviewNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		// 본인 리뷰만 삭제 가능
		if (!review.getUserNo().equals(loginUserNo)) {
			throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
		}

		reviewRepository.delete(review);
	}

	// 숙소별 리뷰 목록 조회
		@Override
		@Transactional(readOnly = true)
		public List<ReviewSummaryDTO> getReviewsByLodging(Long lodgingNo) {
			// 숙소 번호가 없으면 예외
			if (lodgingNo == null) {
				throw new IllegalArgumentException("숙소 번호는 필수입니다.");
			}

			// 특정 숙소의 리뷰들을 조회해서 DTO 리스트로 변환
			return reviewRepository.findByLodgingNoOrderByReviewNoDesc(lodgingNo)
					.stream()
					.map(review -> ReviewSummaryDTO.fromEntity(review, List.of()))
					.toList();
		}

}
