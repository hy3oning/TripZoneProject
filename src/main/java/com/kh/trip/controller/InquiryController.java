package com.kh.trip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.InquiryDTO;
import com.kh.trip.service.InquiryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;



@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/inquiry")
public class InquiryController {
	private final InquiryService service;
	
	@PostMapping("/")
	public Map<String, Long> save(@RequestBody InquiryDTO inquiryDTO) {
		log.info("save()" + inquiryDTO);
		Long ino = service.save(inquiryDTO);
		return Map.of("result", ino);
	}
	
	
	
}
