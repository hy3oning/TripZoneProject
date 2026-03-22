package com.kh.trip.service;

import java.util.List;

import com.kh.trip.domain.InquiryRoom;
import com.kh.trip.dto.InquiryRequestDTO;

public interface InquiryService {
	
	Long createInquiry(InquiryRequestDTO request, Long userNo);

	List<InquiryRoom> findByUserNo(Long userNo);

	InquiryRoom findById(Long inquiryRoomNo);

	void closeInquiry(Long inquiryRoomNo);

	List<InquiryRoom> findAll();

	void updateStatus(Long inquiryRoomNo, String status);
}
