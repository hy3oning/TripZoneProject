package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.BookingDTO;
import com.kh.trip.dto.InquiryDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.service.InquiryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/inquiry")
public class InquiryController {
	private final InquiryService service;

	@PostMapping("/")
	public Map<String, Long> save(@RequestBody InquiryDTO inquiryDTO) {
		log.info("inquiry:save()" + inquiryDTO);
		Long ino = service.save(inquiryDTO);
		return Map.of("result", ino);
	}

	@GetMapping("/list")
	public PageResponseDTO<InquiryDTO> findAll(PageRequestDTO pageRequestDTO) {
		log.info("inquiry:findByAll()");
		return service.findAll(pageRequestDTO);
	}

	@GetMapping("/list/{uno}")
	public PageResponseDTO<InquiryDTO> findByUserId(@PathVariable(name = "uno") Long userNo,
			PageRequestDTO pageRequestDTO) {
		log.info("inquiry:findByUserId(userNo) = " + userNo);
		return service.findByUserId(userNo, pageRequestDTO);
	}
	
	@GetMapping("/{ino}")
	public InquiryDTO findById(@PathVariable(name = "ino") Long inquiryNo) {
		log.info("inquiry:findById(inquiryNo) = " + inquiryNo);
		return service.findById(inquiryNo);
	}
	

	@PatchMapping("/{ino}")
	public void update(@PathVariable(name = "ino") Long inquiryNo, @RequestBody InquiryDTO inquiryDTO) {
		log.info("inquiry:update(inquiryNo) = " + inquiryNo);
		service.update(inquiryNo, inquiryDTO);
	}
	
	@DeleteMapping("/{ino}")
	public void delete(@PathVariable(name = "ino") Long inquiryNo) {
		log.info("inquiry:delete(inquiryNo) = " + inquiryNo);
		service.delete(inquiryNo);
	}
	
}
