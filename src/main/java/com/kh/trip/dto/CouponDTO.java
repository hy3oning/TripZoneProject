package com.kh.trip.dto;

import java.time.LocalDateTime;

import com.kh.trip.domain.enums.CouponStatus;
import com.kh.trip.domain.enums.DiscountType;

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
	private Long adminUserNo;
	private String couponName;
	private DiscountType discountType;
	private Long discountValue;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private CouponStatus status;
	private LocalDateTime regDate;
	private LocalDateTime updDate;
}
