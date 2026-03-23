package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Payment;
import com.kh.trip.domain.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	Optional<Payment> findByPaymentId(String paymentId);

	Optional<Payment> findByBooking_BookingNo(Long bookingNo);

	List<Payment> findByBooking_BookingNoOrderByPaymentNoDesc(Long bookingNo);

	boolean existsByBooking_BookingNoAndPaymentStatusIn(Long bookingNo, List<PaymentStatus> statuses);
}
