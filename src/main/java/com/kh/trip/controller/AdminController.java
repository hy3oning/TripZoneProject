package com.kh.trip.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.AdminUserSearchRequestDTO;
import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.HostProfileService;
import com.kh.trip.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

	private final UserService userService;
	private final HostProfileService hostProfileService;
	
	// 관리자 회원 목록 조회
	@GetMapping("/admin/userlist")
	@PreAuthorize("hasRole('ADMIN')")
	public PageResponseDTO<UserDTO> findUsers(AdminUserSearchRequestDTO request) {
		// 검색조건/페이징 DTO를 서비스로 전달해 회원 목록을 조회한다.
		return userService.findUsers(request);
	}

	// 관리자 회원 상세조회
	@GetMapping("/admin/{userNo}/detail")
	@PreAuthorize("hasRole('ADMIN')")
	public UserDTO getUserDetail(@PathVariable Long userNo) {
		return userService.getUser(userNo);
	}

	// 관리자 호스트승인
	@PatchMapping("/{hostNo}/approve")
	public Map<String, String> approve(@PathVariable Long hostNo, @AuthenticationPrincipal AuthUserPrincipal authUser) {
		hostProfileService.approve(hostNo, authUser.getUserNo());
		return Map.of("result", "SUCCESS");
	}

	//관리자 호스트 승인거절
	@PatchMapping("/{hostNo}/reject")
	public Map<String, String> reject(@PathVariable Long hostNo, @RequestBody HostProfileDTO hostProfileDTO,
			@AuthenticationPrincipal AuthUserPrincipal authUser) {
		hostProfileService.reject(hostNo, authUser.getUserNo(), hostProfileDTO.getRejectReason());
		return Map.of("result", "SUCCESS");
	}
}
