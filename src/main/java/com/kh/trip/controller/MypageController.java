package com.kh.trip.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kh.trip.dto.MypageDTO;
import com.kh.trip.dto.PasswordChangeRequestDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.MypageService;
import com.kh.trip.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

	private final MypageService mypageService;
	private final UserService userService;

	@GetMapping("/home")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.HomeResponse getHome(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getHome(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/profile")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.ProfileResponse getProfile(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getProfile(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/bookings")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.BookingResponse getBookings(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getBookings(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/coupons")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.CouponResponse getCoupons(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getCoupons(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/mileage")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.MileageResponse getMileage(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getMileage(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/payments")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.PaymentResponse getPayments(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getPayments(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/wishlist")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.WishlistResponse getWishlist(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getWishlist(requirePrincipal(principal).getUserNo());
	}

	@GetMapping("/inquiries")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.InquiryResponse getInquiries(@AuthenticationPrincipal AuthUserPrincipal principal) {
		return mypageService.getInquiries(requirePrincipal(principal).getUserNo());
	}

	@PatchMapping("/profile/password")
//	@PreAuthorize("hasRole('USER')")
	public MypageDTO.PasswordChangedResponse changePassword(
			@AuthenticationPrincipal AuthUserPrincipal principal,
			@Valid @RequestBody PasswordChangeRequestDTO request) {
		Long userNo = requirePrincipal(principal).getUserNo();
		userService.changePassword(userNo, request);
		return MypageDTO.PasswordChangedResponse.builder()
				.result("SUCCESS")
				.changedAt(LocalDateTime.now())
				.build();
	}

	private AuthUserPrincipal requirePrincipal(AuthUserPrincipal principal) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용 가능합니다.");
		}
		return principal;
	}
}
