package com.kh.trip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/api/booking")
public class BookingController {
	private final BookingService service;

	@PostMapping("/")
	public Map<String, Long> save(@RequestBody BookingDTO bookingDTO) {
		log.info("save()" + bookingDTO);
		Long bno = service.save(bookingDTO);
		return Map.of("result", bno);
	}

	@GetMapping("/userlist/{uno}")
	public PageResponseDTO<BookingDTO> findByUserId(@PathVariable(name = "uno") Long userNo,
			PageRequestDTO pageRequestDTO) {
		log.info("findByUserId() userNo= " + userNo);
		return service.findByUserId(userNo, pageRequestDTO);
	}

	@GetMapping("/roomlist/{uno}")
	public PageResponseDTO<BookingDTO> findByRoomId(@PathVariable(name = "uno") Long userNo,
			PageRequestDTO pageRequestDTO) {
		log.info("findByLodgingId() lodgingNo= " + userNo);
		return service.findByRoomId(userNo, pageRequestDTO);
	}

	@DeleteMapping("/{bno}")
	public Map<String, String> delete(@PathVariable(name = "bno") Long bookingNo) {
		log.info("findByUserId() = " + bookingNo);
		service.delete(bookingNo);
		return Map.of("result", "SUCCESS");
	}

	@PostMapping("/{bno}/complete")
	public Map<String, String> complete(@PathVariable(name = "bno") Long bookingNo) {
		log.info("complete() bookingNo = " + bookingNo);
		service.complete(bookingNo);
		return Map.of("result", "SUCCESS");
	}
}
