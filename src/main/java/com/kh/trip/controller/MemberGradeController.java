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

import com.kh.trip.dto.MemberGradeDTO;
import com.kh.trip.service.MemberGradeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/membergrade")
public class MemberGradeController {
	
	private final MemberGradeService service;
	
	@PostMapping("/")
	public Map<String, Long> save(@RequestBody MemberGradeDTO memberGradeDTO) {
		log.info("MemberGradeDTO = " + memberGradeDTO);
		Long gno = service.save(memberGradeDTO);
		return Map.of("result",gno);
	}
	
	@GetMapping("/list")
	public List<MemberGradeDTO> findAll() {
		log.info("MemberGradeFindAll()");
		return service.findAll();
	}
	
	@GetMapping("/{gno}")
	public MemberGradeDTO findById(@PathVariable(name = "gno") Long gradeNo) {
		log.info("MemberGradeFindById()"+ gradeNo);
		return service.findById(gradeNo);
	}
	
	@DeleteMapping("/{gno}")
	public Map<String, String> delete(@PathVariable(name = "gno") Long gradeNo) {
		log.info("MemberGradeDelete" + gradeNo);
		service.delete(gradeNo);
		return Map.of("result","SUCCESS");
	}
	
	@PatchMapping("/{gno}")
	public Map<String, String> update(@PathVariable(name = "gno") Long gradeNo, @RequestBody MemberGradeDTO memberGradeDTO) {
		memberGradeDTO.setGradeNo(gradeNo);
		log.info("MemberGradeUpdate" + gradeNo);
		service.update(memberGradeDTO);
		return Map.of("result","SUCCESS");
	}
}
