package com.kh.trip.domain;

import java.time.LocalDateTime;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_payments")
	@SequenceGenerator(name = "seq_payments", sequenceName = "SEQ_PAYMENTS", allocationSize = 1)
	@Column(name = "PAYMENT_NO")
	private Long paymentNo; // 결제 번호

	@OneToOne
	@JoinColumn(name = "BOOKING_NO", nullable = false, unique = true)
	private Booking booking; // 예약 번호

	@Column(name = "PAYMENT_ID", nullable = false, unique = true, length = 100)
	private String paymentId; // 포트원 결제 식별값

	@Column(name = "STORE_ID", length = 100)
	private String storeId; // 포트원 스토어 ID

	@Column(name = "CHANNEL_KEY", length = 200)
	private String channelKey; // 결제 채널 키

	@Column(name = "ORDER_NAME", nullable = false, length = 200)
	private String orderName; // 주문명

	@Column(name = "PAYMENT_AMOUNT", nullable = false)
	private Long paymentAmount; // 결제 금액

	@Column(name = "CURRENCY", nullable = false, length = 10)
	private String currency; // 통화 코드

	@Enumerated(EnumType.STRING)
	@Column(name = "PAY_METHOD", nullable = false, length = 30)
	private String payMethod; // 결제 수단

	@Column(name = "PG_PROVIDER", length = 30)
	private String pgProvider; // PG 제공사

	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_STATUS", nullable = false, length = 20)
	private String paymentStatus; // 결제 상태

	@Column(name = "APPROVED_AT")
	private LocalDateTime approvedAt; // 결제 승인 일시

	@Column(name = "CANCELED_AT")
	private LocalDateTime canceledAt; // 결제 취소 일시

	@Builder.Default
	@Column(name = "REFUND_AMOUNT", nullable = false)
	private Long refundAmount = 0L; // 환불 금액

	@Column(name = "FAIL_REASON", length = 500)
	private String failReason; // 결제 실패 사유

	@Lob
	@Column(name = "RAW_RESPONSE")
	private String rawResponse; // 결제 응답 원본 데이터

}
