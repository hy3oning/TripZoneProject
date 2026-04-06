package com.kh.trip.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomDTO;
import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.HostProfileService;
import com.kh.trip.service.LodgingService;
import com.kh.trip.service.RoomService;

import lombok.RequiredArgsConstructor;

// 객실 관련 요청을 처리하는 컨트롤러
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

	// 객실 비즈니스 로직 처리 서비스
	private final RoomService roomService;

	// 로그인 사용자의 판매자 프로필 조회 서비스
	private final HostProfileService hostProfileService;

	// 객실이 속한 숙소의 소유권 확인용 서비스
	private final LodgingService lodgingService;

	// 객실 등록 API
	// HOST 또는 ADMIN 권한만 가능
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public RoomDTO createRoom(
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestBody RoomDTO roomDTO) {

		// 객실은 반드시 특정 숙소에 속하므로
		// 등록 전에 해당 숙소가 본인 소유인지 검증
		verifyLodgingOwnership(authUser, roomDTO.getLodgingNo());

		return roomService.createRoom(roomDTO);
	}

	// 특정 숙소의 객실 목록 조회
	@GetMapping("/lodging/{lodgingNo}")
	public List<RoomDTO> getRoomsByLodgingNo(@PathVariable Long lodgingNo) {
		return roomService.getRoomsByLodgingNo(lodgingNo);
	}

	// 객실 상세 조회
	@GetMapping("/{roomNo}")
	public RoomDTO getRoomDetail(@PathVariable Long roomNo) {
		return roomService.getRoomDetail(roomNo);
	}

	// 객실 수정 API
	// HOST 또는 ADMIN 권한만 가능
	@PatchMapping("/{roomNo}")
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public RoomDTO updateRoom(
			@PathVariable Long roomNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@RequestBody RoomDTO roomDTO) {

		// 먼저 기존 객실이 내 숙소 소속인지 검증
		verifyRoomOwnership(authUser, roomNo);

		// 객실을 다른 숙소로 옮기는 경우
		// 새 숙소 역시 본인 소유인지 한 번 더 검증
		if (roomDTO.getLodgingNo() != null) {
			verifyLodgingOwnership(authUser, roomDTO.getLodgingNo());
		}

		return roomService.updateRoom(roomNo, roomDTO);
	}

	// 객실 삭제 API
	// HOST 또는 ADMIN 권한만 가능
	@DeleteMapping("/{roomNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public void deleteRoom(
			@PathVariable Long roomNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser) {

		// 삭제할 객실이 본인 숙소의 객실인지 검증
		verifyRoomOwnership(authUser, roomNo);

		roomService.deleteRoom(roomNo);
	}

	// 전체 객실 목록 조회
	@GetMapping
	public List<RoomDTO> getAllRooms() {
		return roomService.getAllRooms();
	}

	// 상태별 객실 목록 조회
	@GetMapping("/status/{status}")
	public List<RoomDTO> getRoomsByStatus(@PathVariable RoomStatus status) {
		return roomService.getRoomsByStatus(status);
	}

	// 객실명 검색
	@GetMapping("/search")
	public List<RoomDTO> searchRoomsByName(@RequestParam String keyword) {
		return roomService.searchRoomsByName(keyword);
	}

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	@GetMapping("/lodging/{lodgingNo}/status/{status}")
	public List<RoomDTO> getRoomsByLodgingNoAndStatus(
			@PathVariable Long lodgingNo,
			@PathVariable RoomStatus status) {
		return roomService.getRoomsByLodgingNoAndStatus(lodgingNo, status);
	}

	// 현재 객실이 본인 숙소 소속 객실인지 검증
	private void verifyRoomOwnership(AuthUserPrincipal authUser, Long roomNo) {

		// 관리자는 모든 객실 관리 가능
		if (isAdmin(authUser)) {
			return;
		}

		// 객실의 lodgingNo를 꺼내서 결국 숙소 소유권 검증으로 연결
		RoomDTO room = roomService.getRoomForManagement(roomNo);
		verifyLodgingOwnership(authUser, room.getLodgingNo());
	}

	// 현재 숙소가 본인 숙소인지 검증
	private void verifyLodgingOwnership(AuthUserPrincipal authUser, Long lodgingNo) {

		// 관리자는 모든 숙소 관리 가능
		if (isAdmin(authUser)) {
			return;
		}

		// 객실은 숙소에 반드시 속해야 하므로 lodgingNo는 필수
		if (lodgingNo == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "숙소 번호는 필수입니다.");
		}

		// 로그인한 사용자의 호스트 프로필 조회
		HostProfileDTO hostProfile = requireHostProfile(authUser);

		// 본인 숙소 목록 안에 현재 lodgingNo가 있는지 확인
		boolean ownsLodging = lodgingService.getLodgingsByHostNo(hostProfile.getHostNo()).stream()
				.anyMatch(item -> lodgingNo.equals(item.getLodgingNo()));

		if (!ownsLodging) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 숙소의 객실만 관리할 수 있습니다.");
		}
	}

	// 판매자 프로필 강제 조회
	private HostProfileDTO requireHostProfile(AuthUserPrincipal authUser) {
		HostProfileDTO hostProfile = hostProfileService.getByUserNo(authUser.getUserNo());

		if (hostProfile == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "판매자 정보를 찾을 수 없습니다.");
		}

		return hostProfile;
	}

	// 관리자 여부 확인
	private boolean isAdmin(AuthUserPrincipal authUser) {
		return authUser != null
				&& authUser.getRoleNames() != null
				&& authUser.getRoleNames().contains("ROLE_ADMIN");
	}
}