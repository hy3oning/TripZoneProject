package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.InquiryRoom;

public interface InquiryRoomRepository extends JpaRepository<InquiryRoom, Long> {
	@Query("select r from InquiryRoom r where r.user.userNo = :userNo order by r.inquiryRoomNo desc")
	List<InquiryRoom> findByUserNo(@Param("userNo") Long userNo);

	List<InquiryRoom> findAllByOrderByInquiryRoomNoDesc();
}
