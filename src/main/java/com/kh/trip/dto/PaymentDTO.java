package com.kh.trip.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
	private Long paymentNo;
	@NotNull(message = "bookingNo는 필수입니다.")
	private Long bookingNo;
	@NotNull(message = "paymentId는 필수입니다.")
	private String paymentId;
	private String storeId;
	private String channelKey;
	private String orderName;
	@NotNull(message = "paymentAmount는 필수입니다.")
	@Positive(message = "paymentAmount는 0보다 커야 합니다.")
	private Long paymentAmount;
	private String currency;
	private String payMethod;
	private String pgProvider;
	private String paymentStatus;
	private LocalDateTime approvedAt;
	private LocalDateTime canceledAt;
	private Long refundAmount;
	private String failReason;
	private String rawResponse;
}
