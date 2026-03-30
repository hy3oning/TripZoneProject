package com.kh.trip.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.BookingDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/seller")
public class SellerController {

	private final BookingService bookingService;
	
	@GetMapping("/hostlist/{hostNo}")
	public PageResponseDTO<BookingDTO> findByHostNo(@PathVariable Long hostNo, PageRequestDTO pageRequestDTO) {
		log.info("findByHostNo() hostNo= " + hostNo);
		return bookingService.findByHostNo(hostNo, pageRequestDTO);
	}
}
