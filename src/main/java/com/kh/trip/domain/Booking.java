package com.kh.trip.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "BOOKINGS")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BOOKINGS")
	@SequenceGenerator(name = "SEQ_BOOKINGS",sequenceName = "SEQ_BOOKINGS", allocationSize = 1)
	@Column(name = "BOOKING_NO")
	private Long bookingNo;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
	@JoinColumn(name = "COUPON_NO", nullable = false) // 실제 DB의 FK 컬럼명
	private Coupon coupon;
	
//	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
//	@JoinColumn(name = "ROOM_NO", nullable = false) // 실제 DB의 FK 컬럼명
//	private Room room;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
	@JoinColumn(name = "USER_COUPON_NO", nullable = false) // 실제 DB의 FK 컬럼명
	private UserCoupon userCoupon;
	
	@Column(name = "CHECK_IN_DATE", nullable = false)
	private LocalDateTime checkInDate;

	@Column(name = "CHECK_OUT_DATE", nullable = false)
	private LocalDateTime checkOutDate;

	@Column(name = "GUEST_COUNT", nullable = false)
	@Positive
	private Long guestCount;
	
	@Column(name = "PRICE_PER_NIGHT", nullable = false)
	@Positive
	private Long pricePerNight;
	
	@Builder.Default
	@Column(name = "DISCOUNT_AMOUNT", nullable = false)
	@Min(0)
	private Long discountAmount = 0L;
	
	@Column(name = "TOTAL_PRICE", nullable = false)
	@Min(0)
	private Long totalPrice;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	@Builder.Default
	private BookingStatus status = BookingStatus.PENDING; // pending, confirmed, canceled, completed
	
	@Column(name = "REQUEST_MESSAGE", length = 500)
	private String requestMessage; 
	
    @Column(name = "REG_DATE", nullable = false, updatable = false)
    @CreationTimestamp
	private LocalDateTime regDate;
	
	@PrePersist
	public void validateNewBooking() {
	    // 1. 체크아웃 > 체크인 검증
		if (checkInDate != null && checkOutDate != null) {
            // 체크아웃이 체크인보다 이전이거나 같으면 안됨
            if (!checkOutDate.isAfter(checkInDate)) {
                throw new IllegalStateException("체크아웃 날짜는 체크인 날짜보다 이후여야 합니다.");
            }
        }
	    
	    // 2. 과거 날짜 예약 방지
	    if (checkInDate.isBefore(LocalDateTime.now())) {
	        throw new IllegalStateException("과거 날짜로는 예약할 수 없습니다.");
	    }
	}
	
	public enum BookingStatus {
		PENDING, // 예약대기
		CONFIRMED, // 예약승인
		CANCELED, // 취소
		COMPLETED  // 숙박완료
	}
	
	public void cancel() {
        if (this.status == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 예약은 자동으로 취소할 수 없습니다.");
        }
        this.status = BookingStatus.CANCELED;
        
        // 연관된 쿠폰이 있다면 복구
        if (this.userCoupon != null) {
            this.userCoupon.restore(); // status를 ACTIVE로 바꾸는 메서드
        }
    }
}
