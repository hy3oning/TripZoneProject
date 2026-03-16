package com.kh.trip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.EventDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.service.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
	
	private final EventService service;
	
	//가로로 보이는 이벤트 리스트
	@GetMapping("/list")
	public PageResponseDTO<EventDTO> list(PageRequestDTO pageRequestDTO) {
		log.info(pageRequestDTO);
		return service.list(pageRequestDTO);
	}
}
