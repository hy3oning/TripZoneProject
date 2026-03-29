package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.InquiryMessageDTO;
import com.kh.trip.dto.InquiryRoomDTO;
import com.kh.trip.service.InquiryMessageService;
import com.kh.trip.service.InquiryRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/inquiry/room")
public class InquiryRoomController {

	private final InquiryRoomService service;
	private final InquiryMessageService messageService;

    // 채팅방 입장 (기존 방이 있으면 반환, 없으면 생성)
    @PostMapping("/")
    public Map<String, Long> save(@RequestBody InquiryRoomDTO roomDTO) {
        log.info("inquiryRoom save() = " + roomDTO);
        Long roomNo = service.save(roomDTO);
        return Map.of("result", roomNo);
    }
    
    // 내 채팅방 목록 조회
    @GetMapping("/{userNo}")
    public List<InquiryRoomDTO> findByUserNo(@PathVariable Long userNo) {
    	 log.info("inquiryRoom findByUserNo() = " + userNo);
        return service.findByUserNo(userNo);
    }
    
    // 이전 메시지 내역 조회
    @GetMapping("/{roomNo}/messages")
	public List<InquiryMessageDTO> findByRoomNo(@PathVariable Long roomNo) {
		log.info("inquiryMessage findByRoomNo() = " + roomNo);
		return messageService.findByRoomNo(roomNo);
	}

    @DeleteMapping("/{roomNo}")
    public void delete(@PathVariable Long roomNo) {
    	 log.info("inquiryRoom delete() = " + roomNo);
         service.delete(roomNo);
    }
}
