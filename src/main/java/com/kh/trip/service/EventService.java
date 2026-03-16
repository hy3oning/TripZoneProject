package com.kh.trip.service;

import org.modelmapper.internal.bytebuddy.asm.Advice.Return;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.dto.EventDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;

@Transactional
public interface EventService {

	PageResponseDTO<EventDTO> list(PageRequestDTO pageRequestDTO);
	
}
