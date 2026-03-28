package com.kh.trip.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.Booking;
import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("select b from Booking b where b.user.userNo = :userNo")
	Page<Booking> findByUserId(@Param("userNo") Long userNo, Pageable pageable);

	@EntityGraph(attributePaths = { "room", "room.lodging", "room.lodging.imageList" })
	@Query("select b from Booking b where b.user.userNo = :userNo order by b.regDate desc")
	List<Booking> findMypageBookings(@Param("userNo") Long userNo);

	@Query("select b from Booking b join b.room r where r.lodging.host.hostNo = :hostNo")
	Page<Booking> findByHostNo(@Param("hostNo") Long hostNo, Pageable pageable);

	@Query("select r.lodging from Booking b join b.room r where b.user.userNo = :userNo")
	List<Lodging> findLodigByUserId(@Param("userNo") Long userNo);

	/**
	 * 리뷰 작성 검증용 상세 조회 bookingNo로 예약 1건을 조회하면서 user, room 정보도 같이 가져온다 이유: 1. 로그인한
	 * 사용자의 예약인지 확인 2. 이 예약이 현재 숙소에 대한 예약인지 확인
	 */
	@Query("select b from Booking b join fetch b.user u join fetch b.room r join fetch r.lodging where b.bookingNo = :bookingNo")
	Optional<Booking> findDetailByBookingNo(@Param("bookingNo") Long bookingNo);

	@Query("select b.bookingNo from Booking b where b.checkOutDate <= :today and b.status = :status")
	List<Long> findBookingNosToComplete(@Param("today")LocalDateTime today,@Param("status") BookingStatus status);

}
