package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.security.AuthUserPrincipal;
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

	@PatchMapping("/{hostNo}/approve")
	public Map<String, String> approve(@PathVariable Long hostNo, @AuthenticationPrincipal AuthUserPrincipal authUser) {
		hostProfileService.approve(hostNo, authUser.getUserNo());
		return Map.of("result", "SUCCESS");
	}

	@PatchMapping("/{hostNo}/reject")
	public Map<String, String> reject(@PathVariable Long hostNo, @RequestBody HostProfileDTO hostProfileDTO,
			@AuthenticationPrincipal AuthUserPrincipal authUser) {
		hostProfileService.reject(hostNo, authUser.getUserNo(), hostProfileDTO.getRejectReason());
		return Map.of("result", "SUCCESS");
	}
}
