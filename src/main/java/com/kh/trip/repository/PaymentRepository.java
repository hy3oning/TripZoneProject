package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Payment;
import com.kh.trip.domain.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findFirstByBooking_BookingNoOrderByPaymentNoDesc(Long bookingNo);

	Page<Payment> findByBooking_BookingNoOrderByPaymentNoDesc(Long bookingNo, Pageable pageable);

	boolean existsByBooking_BookingNoAndPaymentStatusIn(Long bookingNo, List<PaymentStatus> statuses);
}
