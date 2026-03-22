package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.InquiryMessage;

public interface InquiryMessageRepository extends JpaRepository<InquiryMessage, Long> {
	@Query("select m from InquiryMessage m where m.inquiryRoom.inquiryRoomNo = :roomNo order by m.regDate asc")
	List<InquiryMessage> findByInquiryRoomNo(@Param("roomNo") Long roomNo);
}
