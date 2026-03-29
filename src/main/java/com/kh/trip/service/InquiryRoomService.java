package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.InquiryRoomDTO;

public interface InquiryRoomService {

	Long save(InquiryRoomDTO roomDTO);

	List<InquiryRoomDTO> findByUserNo(Long userNo);

	void delete(Long roomNo);

}
