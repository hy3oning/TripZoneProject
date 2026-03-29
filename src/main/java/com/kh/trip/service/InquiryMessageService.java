package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.InquiryMessageDTO;

public interface InquiryMessageService {

	List<InquiryMessageDTO> findByRoomNo(Long roomNo);

	InquiryMessageDTO save(InquiryMessageDTO messageDTO);

}
