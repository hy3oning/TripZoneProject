package com.kh.trip.domain;

import java.time.LocalDateTime;

import com.kh.trip.domain.enums.CouponStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "USER_COUPONS")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCoupon {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_COUPONS")
	@SequenceGenerator(name = "SEQ_USER_COUPONS", sequenceName = "SEQ_USER_COUPONS", allocationSize = 1)
	@Column(name = "USER_COUPON_NO")
	private Long userCouponNo;

	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
	@JoinColumn(name = "USER_NO", nullable = false) // 실제 DB의 FK 컬럼명
	private User user;

	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
	@JoinColumn(name = "COUPON_NO", nullable = false) // 실제 DB의 FK 컬럼명
	private Coupon coupon;

	@Column(name = "ISSUED_AT", nullable = false)
	private LocalDateTime issuedAt;

	@Column(name = "USED_AT")
	private LocalDateTime usedAt;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	@Builder.Default
	private CouponStatus status = CouponStatus.ACTIVE; // active,expired, used

	public CouponStatus determineFinalStatus() {
		// 1. 이미 사용했다면 무조건 USED
		if (this.status.equals(CouponStatus.USED) || this.usedAt != null) {
			return CouponStatus.USED;
		}

		// 2. 사용 전이라면 원본 쿠폰(Master)의 상태를 따라감
		return this.coupon.getStatus();
	}

	public void restore() {
		// 이미 사용 가능 상태라면 복구할 필요가 없음 (방어 코드)
		if (this.status == CouponStatus.ACTIVE) {
			return;
		}

		// 사용 완료된 쿠폰만 복구 가능하게 제한을 둘 수도 있습니다.
		this.status = CouponStatus.ACTIVE;
		this.usedAt = null; // 사용 일시 초기화
	}
}
