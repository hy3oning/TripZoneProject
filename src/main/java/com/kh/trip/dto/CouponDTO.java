package com.kh.trip.dto;

import java.time.LocalDateTime;

import com.kh.trip.domain.Coupon.DiscountType;
import com.kh.trip.domain.enums.CouponStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {
	private Long couponNo;
	private String couponName;
	private DiscountType discountType;
	private Long discountValue;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private CouponStatus status;
	private LocalDateTime regDate;
	private LocalDateTime updDate;
}
