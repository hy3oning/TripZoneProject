package com.kh.trip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.WishListDTO;
import com.kh.trip.service.WishListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishList")
public class WishListController {
	
	private final WishListService service;
	//리스트
	@GetMapping("/list")
	public PageResponseDTO<WishListDTO> findAll(PageRequestDTO pageRequestDTO){
		log.info(pageRequestDTO);
		return service.findAll(pageRequestDTO);
	}
	//찜 저장
	@PostMapping("/")
	public Map<String, Long> save(@RequestBody WishListDTO wishListDTO){
		log.info("WishListDTO"+wishListDTO);
		Long wno = service.save(wishListDTO);
		return Map.of("WNO",wno);
	}
	//찜 삭제
	@DeleteMapping("/{wno}")
	public Map<String, String> delete(@PathVariable Long wno){
		log.info("Delete:"+wno);
		service.delete(wno);
		return Map.of("RESULT","SUCCESS");
	}
}
