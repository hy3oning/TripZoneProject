package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.Payment;
import com.kh.trip.domain.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	Optional<Payment> findByPaymentId(String paymentId);

	Optional<Payment> findByBooking_BookingNo(Long bookingNo);

	List<Payment> findByBooking_BookingNoOrderByPaymentNoDesc(Long bookingNo);

	@EntityGraph(attributePaths = { "booking", "booking.room", "booking.room.lodging" })
	@Query("select p from Payment p where p.booking.user.userNo = :userNo order by p.regDate desc")
	List<Payment> findMypagePayments(@Param("userNo") Long userNo);

	boolean existsByBooking_BookingNoAndPaymentStatusIn(Long bookingNo, List<PaymentStatus> statuses);
}
