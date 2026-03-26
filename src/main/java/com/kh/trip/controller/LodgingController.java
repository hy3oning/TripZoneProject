package com.kh.trip.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.service.LodgingService;
import com.kh.trip.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lodgings")
@RequiredArgsConstructor
public class LodgingController {

	private final LodgingService lodgingService;
	private final CustomFileUtil fileUtil; 

	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public LodgingDTO createLodging(@ModelAttribute LodgingDTO lodgingDTO) {
		return lodgingService.createLodging(lodgingDTO);
	}

	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}

	@GetMapping("/{lodgingNo}")
	public LodgingDTO getLodging(@PathVariable Long lodgingNo) {
		return lodgingService.getLodging(lodgingNo);
	}

	@GetMapping("/list")
	public List<LodgingDTO> getAllLodgings() {
		return lodgingService.getAllLodgings();
	}

	@GetMapping("/region")
	public List<LodgingDTO> getLodgingsByRegion(@RequestParam String region) {
		return lodgingService.getLodgingsByRegion(region);
	}

	@GetMapping("/search")
	public List<LodgingDTO> searchLodgingsByName(@RequestParam String keyword) {
		return lodgingService.searchLodgingsByName(keyword);
	}

	@PatchMapping("/{lodgingNo}")
	public LodgingDTO updateLodging(@PathVariable Long lodgingNo, @ModelAttribute LodgingDTO lodgingDTO) {
		lodgingDTO.setLodgingNo(lodgingNo);
		return lodgingService.updateLodging(lodgingNo, lodgingDTO);
	}

	@DeleteMapping("/{lodgingNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteLodging(@PathVariable Long lodgingNo) {
		lodgingService.deleteLodging(lodgingNo);
	}

	@GetMapping("/{lodgingNo}/detail") // 상세보기 전용 API
	public LodgingDTO getLodgingDetail(@PathVariable Long lodgingNo) {
		return lodgingService.getLodgingDetail(lodgingNo); // 상세 DTO 반환
	}
}