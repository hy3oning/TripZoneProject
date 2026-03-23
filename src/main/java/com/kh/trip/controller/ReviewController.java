package com.kh.trip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.ReviewCreateDTO;
import com.kh.trip.dto.ReviewSummaryDTO;
import com.kh.trip.dto.ReviewUpdateDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.ReviewService;

import lombok.RequiredArgsConstructor;

/**
 * 리뷰 컨트롤러
 * 
 * 리뷰 관련 요청을 처리하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	// 리뷰 서비스 주입
	private final ReviewService reviewService;

	/**
	 * 리뷰 등록
	 * 
	 * 요청 예시: POST /api/v1/reviews
	 * 
	 * 설명: - 로그인한 사용자만 리뷰를 등록할 수 있다
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewSummaryDTO createReview(@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestBody ReviewCreateDTO reviewCreateDTO) {

		// 로그인한 사용자 정보가 없으면 예외 발생
		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 작성할 수 있습니다.");
		}

		// 로그인한 사용자 번호와 리뷰 작성 DTO를 서비스로 전달
		return reviewService.createReview(authUser.getUserNo(), reviewCreateDTO);
	}

	/**
	 * 리뷰 수정 
	 */
	@PatchMapping("/{reviewNo}")
	public ReviewSummaryDTO updateReview(@PathVariable Long reviewNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser, @RequestBody ReviewUpdateDTO reviewUpdateDTO) {

		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 수정할 수 있습니다.");
		}

		return reviewService.updateReview(authUser.getUserNo(), reviewNo, reviewUpdateDTO);
	}

	/**
	 * 리뷰 삭제 
	 */
	@DeleteMapping("/{reviewNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteReview(@PathVariable Long reviewNo, @AuthenticationPrincipal AuthUserPrincipal authUser) {

		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 삭제할 수 있습니다.");
		}

		reviewService.deleteReview(authUser.getUserNo(), reviewNo);
	}
	
	/**
	 * 숙소별 리뷰 목록 조회
	 */
	@GetMapping("/lodgings/{lodgingNo}")
	public List<ReviewSummaryDTO> getReviewsByLodging(@PathVariable Long lodgingNo) {
		return reviewService.getReviewsByLodging(lodgingNo);
	}

}