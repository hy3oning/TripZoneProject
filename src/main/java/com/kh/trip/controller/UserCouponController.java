package com.kh.trip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserCouponDTO;
import com.kh.trip.service.UserCouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;



@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usercoupon")
public class UserCouponController {
	private final UserCouponService service;
	
	@PostMapping("/")
	public Map<String, Long> save(@RequestBody UserCouponDTO userCouponDTO) {
		log.info("save() userCouponDTO = " + userCouponDTO);
		Long ucno = service.save(userCouponDTO);
		return Map.of("userCouponNo", ucno);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<UserCouponDTO> findAll(PageRequestDTO pageRequestDTO) {
		log.info("findAll() = " + pageRequestDTO);
		return service.findAll(pageRequestDTO);
	}
}
