package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.service.HostProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hosts")
public class HostProfileController {

	private final HostProfileService hostProfileService;

	@PostMapping("/register")
	public Long register(@RequestBody HostProfileDTO hostProfileDTO) {
		return hostProfileService.register(hostProfileDTO);
	}

	@GetMapping
	public List<HostProfileDTO> getList() {
		return hostProfileService.getList();
	}

	@GetMapping("/{hostNo}")
	public HostProfileDTO get(@PathVariable Long hostNo) {
		return hostProfileService.get(hostNo);
	}


	@PatchMapping("/{hostNo}")
	public Map<String, String> update(@PathVariable Long hostNo, @RequestBody HostProfileDTO hostProfileDTO) {
		hostProfileService.update(hostNo, hostProfileDTO);
		return Map.of("result", "SUCCESS");
	}

	@PutMapping("/{hostNo}/delete")
	public Map<String, String> delete(@PathVariable Long hostNo) {
		hostProfileService.delete(hostNo);
		return Map.of("result", "SUCCESS");
	}

	@PutMapping("/{hostNo}/restore")
	public Map<String, String> restore(@PathVariable Long hostNo) {
		hostProfileService.restore(hostNo);
		return Map.of("result", "SUCCESS");
	}

}
