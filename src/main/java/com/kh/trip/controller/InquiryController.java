package com.kh.trip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.kh.trip.dto.InquiryRequestDTO;
import com.kh.trip.dto.InquiryResponseDTO;
import com.kh.trip.repository.InquiryMessageRepository;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.InquiryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {
	private final InquiryService inquiryService;
	private final InquiryMessageRepository inquiryMessageRepository;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Long> createInquiry(@Valid @RequestBody InquiryRequestDTO request,
			@AuthenticationPrincipal AuthUserPrincipal principal) {
		AuthUserPrincipal authUser = requirePrincipal(principal);
		Long inquiryId = inquiryService.createInquiry(request, authUser.getUserNo());
		return Map.of("result", inquiryId);
	}

	@GetMapping
	public List<InquiryResponseDTO> getMyInquiries(@RequestParam Long userId,
			@AuthenticationPrincipal AuthUserPrincipal principal) {
		AuthUserPrincipal authUser = requirePrincipal(principal);
		if (!authUser.getUserNo().equals(userId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own inquiries.");
		}

		return inquiryService.findByUserNo(userId).stream().map(room -> InquiryResponseDTO.fromEntity(room,
				inquiryMessageRepository.findByInquiryRoomNo(room.getInquiryRoomNo()))).toList();
	}

	@GetMapping("/{inquiryId}")
	public InquiryResponseDTO getInquiry(@PathVariable Long inquiryId,
			@AuthenticationPrincipal AuthUserPrincipal principal) {
		AuthUserPrincipal authUser = requirePrincipal(principal);
		var room = inquiryService.findById(inquiryId);
		validateInquiryAccess(authUser, room.getUser() != null ? room.getUser().getUserNo() : null);
		return InquiryResponseDTO.fromEntity(room,
				inquiryMessageRepository.findByInquiryRoomNo(room.getInquiryRoomNo()));
	}

	@PatchMapping("/{inquiryId}")
	public Map<String, String> updateInquiry(@PathVariable Long inquiryId, @RequestBody Map<String, String> body,
			@AuthenticationPrincipal AuthUserPrincipal principal) {
		AuthUserPrincipal authUser = requirePrincipal(principal);
		var room = inquiryService.findById(inquiryId);
		validateInquiryAccess(authUser, room.getUser() != null ? room.getUser().getUserNo() : null);
		inquiryService.updateStatus(inquiryId, body.getOrDefault("inquiryStatus", "PENDING"));
		return Map.of("result", "SUCCESS");
	}

	@DeleteMapping("/{inquiryId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteInquiry(@PathVariable Long inquiryId, @AuthenticationPrincipal AuthUserPrincipal principal) {
		AuthUserPrincipal authUser = requirePrincipal(principal);
		var room = inquiryService.findById(inquiryId);
		validateInquiryAccess(authUser, room.getUser() != null ? room.getUser().getUserNo() : null);
		inquiryService.closeInquiry(inquiryId);
	}

	private AuthUserPrincipal requirePrincipal(AuthUserPrincipal principal) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required.");
		}
		return principal;
	}

	private void validateInquiryAccess(AuthUserPrincipal principal, Long ownerUserNo) {
		boolean isAdmin = principal.getRoleNames() != null && principal.getRoleNames().contains("ROLE_ADMIN");
		if (!isAdmin && (ownerUserNo == null || !principal.getUserNo().equals(ownerUserNo))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this inquiry.");
		}
	}

}
