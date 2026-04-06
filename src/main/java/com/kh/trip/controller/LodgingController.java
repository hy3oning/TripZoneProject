package com.kh.trip.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.server.ResponseStatusException;

import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.security.AuthUserPrincipal;
import com.kh.trip.service.HostProfileService;
import com.kh.trip.service.LodgingService;
import com.kh.trip.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;

// 숙소 관련 요청을 처리하는 컨트롤러
// 클라이언트의 요청을 받아서 필요한 권한 검사 후 서비스 계층으로 전달한다.
@RestController
@RequestMapping("/api/lodgings")
@RequiredArgsConstructor
public class LodgingController {

	// 숙소 비즈니스 로직을 처리하는 서비스
	private final LodgingService lodgingService;

	// 현재 로그인한 사용자의 판매자(호스트) 정보를 조회하기 위한 서비스
	private final HostProfileService hostProfileService;

	// 업로드된 숙소 이미지 파일을 조회할 때 사용하는 유틸 클래스
	private final CustomFileUtil fileUtil;

	// 숙소 등록 API
	// HOST 또는 ADMIN 권한이 있는 사용자만 등록할 수 있다.
	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public LodgingDTO createLodging(
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@ModelAttribute LodgingDTO lodgingDTO) {

		// 관리자가 아닌 일반 판매자인 경우
		// hostNo를 클라이언트가 직접 보내게 하지 않고
		// 로그인한 사용자 기준의 hostNo를 서버에서 강제로 세팅한다.
		if (!isAdmin(authUser)) {
			HostProfileDTO hostProfile = requireHostProfile(authUser);
			lodgingDTO.setHostNo(hostProfile.getHostNo());

		// 관리자일 경우에는 특정 호스트 소속으로 숙소를 등록할 수 있으므로
		// hostNo가 반드시 필요하다.
		} else if (lodgingDTO.getHostNo() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "관리자 등록은 hostNo가 필요합니다.");
		}

		// 최종 숙소 등록 처리는 서비스로 위임
		return lodgingService.createLodging(lodgingDTO);
	}

	// 업로드된 숙소 이미지 파일 조회 API
	// fileName을 받아 실제 이미지 Resource를 반환한다.
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}

	// 숙소 단건 조회 API
	// 숙소 번호로 숙소 기본 정보를 조회한다.
	@GetMapping("/{lodgingNo}")
	public LodgingDTO getLodging(@PathVariable Long lodgingNo) {
		return lodgingService.getLodging(lodgingNo);
	}

	// 숙소 전체 목록 조회 API
	// 일반 사용자 화면에서 숙소 목록을 불러올 때 사용한다.
	@GetMapping("/list")
	public List<LodgingDTO> getAllLodgings() {
		return lodgingService.getAllLodgings();
	}

	// 숙소 페이징 목록 조회 API
	// 페이지 번호와 페이지 크기를 받아서 페이지 단위로 숙소 목록을 조회한다.
	@GetMapping("/page")
	public PageResponseDTO<LodgingDTO> getAllLodgings(PageRequestDTO pageRequestDTO) {
		return lodgingService.getAllLodgings(pageRequestDTO);
	}

	// 지역별 숙소 조회 API
	// 예: 서울, 부산, 제주 등 지역명을 기준으로 숙소를 조회한다.
	@GetMapping("/region")
	public List<LodgingDTO> getLodgingsByRegion(@RequestParam String region) {
		return lodgingService.getLodgingsByRegion(region);
	}

	// 숙소명 검색 API
	// 사용자가 입력한 키워드가 숙소명에 포함된 숙소를 검색한다.
	@GetMapping("/search")
	public List<LodgingDTO> searchLodgingsByName(@RequestParam String keyword) {
		return lodgingService.searchLodgingsByName(keyword);
	}

	// 숙소 수정 API
	// HOST 또는 ADMIN 권한이 있어야 하며,
	// 일반 판매자는 본인 소유의 숙소만 수정할 수 있다.
	@PatchMapping("/{lodgingNo}")
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public LodgingDTO updateLodging(
			@PathVariable Long lodgingNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser,
			@ModelAttribute LodgingDTO lodgingDTO) {

		// 수정 대상 숙소가 현재 로그인한 사용자의 숙소인지 검증
		verifyLodgingOwnership(authUser, lodgingNo);

		// URL에서 받은 lodgingNo를 DTO에도 세팅해서
		// 어떤 숙소를 수정하는지 명확하게 맞춘다.
		lodgingDTO.setLodgingNo(lodgingNo);

		// 수정 처리 서비스 호출
		return lodgingService.updateLodging(lodgingNo, lodgingDTO);
	}

	// 숙소 삭제 API
	// 실제 물리 삭제가 아니라 상태를 INACTIVE로 바꾸는 소프트 삭제 방식으로 동작한다.
	@DeleteMapping("/{lodgingNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
	public void deleteLodging(
			@PathVariable Long lodgingNo,
			@AuthenticationPrincipal AuthUserPrincipal authUser) {

		// 삭제 대상 숙소가 현재 로그인한 사용자의 숙소인지 검증
		verifyLodgingOwnership(authUser, lodgingNo);

		// 삭제 처리 서비스 호출
		lodgingService.deleteLodging(lodgingNo);
	}

	// 숙소 상세보기 API
	// 기본 숙소 정보 + 이미지 + 객실 목록 + 리뷰 요약 등을 함께 조회하는 용도
	@GetMapping("/{lodgingNo}/detail")
	public LodgingDTO getLodgingDetail(@PathVariable Long lodgingNo) {
		return lodgingService.getLodgingDetail(lodgingNo);
	}

	// 현재 로그인한 사용자가 해당 숙소의 소유자인지 검증하는 메서드
	private void verifyLodgingOwnership(AuthUserPrincipal authUser, Long lodgingNo) {

		// 관리자는 모든 숙소에 접근할 수 있으므로 바로 통과
		if (isAdmin(authUser)) {
			return;
		}

		// 일반 판매자는 자신의 hostProfile을 조회한 뒤
		// 본인 소유 숙소 목록에 현재 lodgingNo가 있는지 확인한다.
		HostProfileDTO hostProfile = requireHostProfile(authUser);

		boolean ownsLodging = lodgingService.getLodgingsByHostNo(hostProfile.getHostNo()).stream()
				.anyMatch(item -> lodgingNo.equals(item.getLodgingNo()));

		// 본인 소유 숙소가 아니면 수정/삭제를 차단한다.
		if (!ownsLodging) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 숙소만 수정할 수 있습니다.");
		}
	}

	// 로그인한 사용자의 판매자 프로필을 반드시 조회하는 메서드
	// 판매자 프로필이 없으면 HOST 권한 작업을 할 수 없다.
	private HostProfileDTO requireHostProfile(AuthUserPrincipal authUser) {
		HostProfileDTO hostProfile = hostProfileService.getByUserNo(authUser.getUserNo());

		if (hostProfile == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "판매자 정보를 찾을 수 없습니다.");
		}

		return hostProfile;
	}

	// 현재 로그인 사용자가 관리자인지 확인하는 메서드
	private boolean isAdmin(AuthUserPrincipal authUser) {
		return authUser != null
				&& authUser.getRoleNames() != null
				&& authUser.getRoleNames().contains("ROLE_ADMIN");
	}
}