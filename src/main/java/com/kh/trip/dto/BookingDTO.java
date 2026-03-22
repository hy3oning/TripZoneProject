package com.kh.trip.dto;

import java.time.LocalDateTime;

import com.kh.trip.domain.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
	private Long bookingNo;
	private Long userNo;
	private Long roomNo;
	private Long userCouponNo;
	private LocalDateTime checkInDate;
	private LocalDateTime checkOutDate;
	private Long guestCount;
	private Long pricePerNight;
	private Long discountAmount;
	private Long totalPrice;
	private BookingStatus status;
	private String requestMessage;
	private LocalDateTime regDate;
}
