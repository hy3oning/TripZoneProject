package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.MileageHistory;
import com.kh.trip.domain.enums.MileageChangeType;
import com.kh.trip.domain.enums.MileageStatus;

public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {

	List<MileageHistory> findByPayment_PaymentNo(Long paymentNo);

	@Query("""
			select mh
			from MileageHistory mh
			left join fetch mh.booking
			left join fetch mh.payment
			where mh.user.userNo = :userNo
			order by mh.regDate desc
			""")
	List<MileageHistory> findMypageMileage(@Param("userNo") Long userNo);

	boolean existsByPayment_PaymentNoAndChangeTypeAndStatus(Long paymentNo, MileageChangeType changeType,
			MileageStatus status);
}
