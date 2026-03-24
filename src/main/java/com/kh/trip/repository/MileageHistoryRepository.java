package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.MileageHistory;

public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {

	List<MileageHistory> findByPayment_PaymentNo(Long paymentNo);
}
