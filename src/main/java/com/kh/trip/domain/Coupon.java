package com.kh.trip.domain;

import java.time.LocalDateTime;

import com.kh.trip.domain.common.BaseTimeEntity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Coupons")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_COUPONS")
	@SequenceGenerator(name = "SEQ_COUPONS", sequenceName = "SEQ_COUPONS", allocationSize = 1)
	@Column(name = "COUPON_NO")
	private Long couponNo;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
	@JoinColumn(name = "ISSUED_BY_USER_NO", nullable = false) // 실제 DB의 FK 컬럼명
	private User user;
	
	@Column(name = "COUPON_NAME", length = 100, unique = true, nullable = false)
	private String couponName;
	
	@Column(name = "DISCOUNT_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private DiscountType discountType;
	
	@Column(name = "DISCOUNT_VALUE", nullable = false)
	@Positive
	private Long discountValue;
	
	@Column(name = "START_DATE", nullable = false)
	private LocalDateTime startDate;
	
	@Column(name = "END_DATE", nullable = false)
	private LocalDateTime endDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	@Builder.Default
	private CouponStatus status = CouponStatus.INACTIVE; // active, inactive, delete, expiration
	
	@PrePersist
	@PreUpdate
	public void validateDates() {
	    if (startDate != null && endDate != null) {
	        // endDate가 startDate와 같거나 그보다 이전이면 에러 발생
	        if (!endDate.isAfter(startDate)) { 
	            throw new IllegalStateException("종료 일시는 시작 일시보다 최소 1초 이상 뒤여야 합니다.");
	        }
	    }
	}
	
	public enum DiscountType{
		AMOUNT {
	        @Override
	        public Long calculate(Long price, Long discount) { return price - discount; }
	    },
	    PERCENT {
	        @Override
	        public Long calculate(Long price, Long discount) { return price - (price * discount / 100); }
	    };

	    // 추상 메서드를 선언해서 각 항목이 직접 계산하게 함
	    public abstract Long calculate(Long price, Long discount);
	}
	
}
