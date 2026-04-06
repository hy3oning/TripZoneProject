package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.trip.dto.ReviewAdminDTO;
import com.kh.trip.dto.ReviewDTO;
import com.kh.trip.dto.ReviewStatsDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.ReviewService;

import lombok.RequiredArgsConstructor;

// 리뷰 관련 요청을 처리하는 REST 컨트롤러
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	// 리뷰 비즈니스 로직 처리 서비스
	private final ReviewService reviewService;

	// 리뷰 등록 API
	// USER 권한을 가진 로그인 사용자만 작성 가능
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('USER')")
	public ReviewDTO createReview(
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestBody ReviewDTO reviewDTO) {

		// 로그인 정보가 없으면 리뷰 작성 불가
		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 작성할 수 있습니다.");
		}

		// 로그인 사용자 번호와 리뷰 DTO를 서비스에 전달
		return reviewService.createReview(authUser.getUserNo(), reviewDTO);
	}

	// 리뷰 이미지 업로드 API
	@PostMapping("/images")
	@PreAuthorize("hasRole('USER')")
	public Map<String, List<String>> uploadReviewImages(
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestParam List<MultipartFile> files) {

		// 로그인 정보가 없으면 업로드 불가
		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰 이미지를 업로드할 수 있습니다.");
		}

		// 업로드된 파일명을 imageUrls 라는 키로 반환
		return Map.of("imageUrls", reviewService.uploadReviewImages(files));
	}

	// 리뷰 수정 API
	@PatchMapping("/{reviewNo}")
	@PreAuthorize("hasRole('USER')")
	public ReviewDTO updateReview(
			@PathVariable Long reviewNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestBody ReviewDTO reviewDTO) {

		// 로그인 정보가 없으면 수정 불가
		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 수정할 수 있습니다.");
		}

		return reviewService.updateReview(authUser.getUserNo(), reviewNo, reviewDTO);
	}

	// 리뷰 삭제 API
	@DeleteMapping("/{reviewNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('USER')")
	public void deleteReview(
			@PathVariable Long reviewNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser) {

		// 로그인 정보가 없으면 삭제 불가
		if (authUser == null) {
			throw new IllegalArgumentException("로그인한 사용자만 리뷰를 삭제할 수 있습니다.");
		}

		reviewService.deleteReview(authUser.getUserNo(), reviewNo);
	}

	// 특정 숙소의 리뷰 목록 조회 API
	@GetMapping("/lodgings/{lodgingNo}")
	public List<ReviewDTO> getReviewsByLodging(@PathVariable Long lodgingNo) {
		return reviewService.getReviewsByLodging(lodgingNo);
	}

	// 특정 숙소의 리뷰 통계 조회 API
	@GetMapping("/lodgings/{lodgingNo}/stats")
	public ReviewStatsDTO getReviewStatsByLodging(@PathVariable Long lodgingNo) {
		return reviewService.getReviewStatsByLodging(lodgingNo);
	}

	// 관리자용 리뷰 목록 조회 API
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public List<ReviewAdminDTO> getAdminReviews() {
		return reviewService.getAdminReviews();
	}

	// 관리자용 리뷰 공개/숨김 상태 변경 API
	@PatchMapping("/{reviewNo}/visibility")
	@PreAuthorize("hasRole('ADMIN')")
	public ReviewAdminDTO updateReviewVisibility(
			@PathVariable Long reviewNo,
			@RequestBody Map<String, String> payload) {

		// payload 안의 status 값을 꺼내 서비스로 전달
		return reviewService.updateReviewVisibility(reviewNo, payload.get("status"));
	}
}