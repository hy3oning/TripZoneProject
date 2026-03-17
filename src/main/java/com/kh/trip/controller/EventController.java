package com.kh.trip.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public PageResponseDTO<EventDTO> findAll(PageRequestDTO pageRequestDTO) {
		log.info(pageRequestDTO);
		return service.findAll(pageRequestDTO);
	}
	//이벤트변경,새로운거 저장
	@PostMapping("/")
	public Map<String, Long> save(@RequestBody EventDTO eventDTO){
		log.info("EventDTO:"+eventDTO);
		Long eno = service.save(eventDTO);
		return Map.of("ENO",eno);
	}
	//수정
	@PutMapping("/{eno}")
	public Map<String, String> update(@PathVariable (name="eno") Long eno, @RequestBody EventDTO eventDTO){
		eventDTO.setEventNo(eno);
		log.info("Update:"+eventDTO);
		service.update(eventDTO);
		
		return Map.of("RESULT","SUCCESS");
	}
	//삭제
	@DeleteMapping("/{eno}")
	public Map<String, String> delete(@PathVariable Long eno) {
		log.info("Delete: " + eno);
		service.delete(eno);
		return Map.of("RESULT", "SUCCESS");
	}
}
