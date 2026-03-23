package com.kh.trip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{
	@Query("select b from Booking b where b.user.userNo = :userNo")
	Page<Booking> findByUserId(@Param("userNo") Long userNo, Pageable pageable);

	@Query("select b from Booking b join b.room r join Lodging l on r.lodgingNo = l.lodgingNo where l.hostNo = :userNo")
	Page<Booking> findByRoomId(@Param("userNo")Long userNo, Pageable pageable);
	
}
