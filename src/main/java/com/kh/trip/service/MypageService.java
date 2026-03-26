package com.kh.trip.service;

import com.kh.trip.dto.MypageDTO;

public interface MypageService {

	MypageDTO.HomeResponse getHome(Long userNo);

	MypageDTO.ProfileResponse getProfile(Long userNo);

	MypageDTO.BookingResponse getBookings(Long userNo);

	MypageDTO.BookingCreatedResponse createBooking(Long userNo, MypageDTO.BookingCreateRequest request);

	MypageDTO.CouponResponse getCoupons(Long userNo);

	MypageDTO.MileageResponse getMileage(Long userNo);

	MypageDTO.PaymentResponse getPayments(Long userNo);

	MypageDTO.WishlistResponse getWishlist(Long userNo);

	MypageDTO.InquiryResponse getInquiries(Long userNo);
}
