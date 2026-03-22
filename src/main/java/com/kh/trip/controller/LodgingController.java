package com.kh.trip.controller;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
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

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.Room;
import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.dto.LodgingDetailDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.service.LodgingService;

import lombok.RequiredArgsConstructor;

@RestController // 이 클래스가 REST API 컨트롤러라는 뜻
// 메서드 반환값을 JSON 형태로 응답
@RequestMapping("/api/v1/lodgings")
@RequiredArgsConstructor
public class LodgingController {

	private final LodgingService lodgingService;

	/**
	 * 숙소 등록 요청: POST /api/lodgings 흐름 1. 프론트가 JSON 데이터를 보낸다. 2. @RequestBody가 JSON을
	 * LodgingDTO로 바꿔준다. 3. DTO를 Entity로 변환한다. 4. Service에 저장 요청을 보낸다. 5. 저장된 결과를 다시
	 * DTO로 바꿔서 응답한다.
	 * 
	 * @ResponseStatus(HttpStatus.CREATED) - 등록 성공 시 201 Created 상태코드 반환
	 */
	@PostMapping("/")
	@ResponseStatus(HttpStatus.CREATED)
	public LodgingDTO createLodging(@RequestBody LodgingDTO lodgingDTO) {
		Lodging lodging = lodgingDTO.toEntity();
		List<Room> roomList = lodgingDTO.getRoomDTO().stream().map(roomDTO -> {
					Room room = RoomSummaryDTO.toEntity(roomDTO);
					room.changeLodgingNo(lodging.getLodgingNo());
					return room;
				}).collect(Collectors.toList());
		Lodging savedLodging = lodgingService.createLodging(lodging, roomList);
		return LodgingDTO.fromEntity(savedLodging);
	}

	/**
	 * 숙소 단건 조회
	 * 
	 * 요청 예시: GET /api/lodgings/1
	 * 
	 * @PathVariable - URL 경로의 값을 변수로 받아온다. - 여기서는 lodgingNo 자리에 들어온 숫자를 Long 타입으로
	 *               받는다.
	 */
	@GetMapping("/{lodgingNo}")
	public LodgingDTO getLodging(@PathVariable Long lodgingNo) {
		Lodging lodging = lodgingService.getLodging(lodgingNo);
		return LodgingDTO.fromEntity(lodging);
	}

	/**
	 * 숙소 전체 목록 조회
	 * 
	 * 요청 예시: GET /api/lodgings
	 * 
	 * 동작: - DB에 있는 숙소 전체 목록을 조회 - Entity 리스트를 DTO 리스트로 변환해서 응답
	 * 
	 * stream() - 리스트 안의 데이터를 하나씩 꺼내서 가공할 때 사용 map(...) - 각 Lodging 객체를 LodgingDTO로
	 * 바꾼다 toList() - 다시 리스트로 모은다
	 */

	@GetMapping // 화면은 바뀌지 않으므로 경로 x
	public List<LodgingDTO> getAllLodgings() {
		return lodgingService.getAllLodgings().stream().map(LodgingDTO::fromEntity).toList();
	}

	/**
	 * 지역으로 숙소 목록 조회
	 * 
	 * 요청 예시: GET /api/lodgings/region?region=서울
	 * 
	 * @RequestParam - URL 쿼리스트링 값을 받아온다.
	 */
	@GetMapping("/region")
	public List<LodgingDTO> getLodgingsByRegion(@RequestParam String region) {
		return lodgingService.getLodgingsByRegion(region).stream().map(LodgingDTO::fromEntity).toList();
	}

	/**
	 * 숙소명 키워드 검색
	 * 
	 * 요청 예시: GET /api/lodgings/search?keyword=호텔
	 * 
	 * 예:"호텔"이 포함된 숙소명 검색
	 * 
	 */
	@GetMapping("/search")
	public List<LodgingDTO> searchLodgingsByName(@RequestParam String keyword) {
		return lodgingService.searchLodgingsByName(keyword).stream().map(LodgingDTO::fromEntity).toList();
	}

	/**
	 * 숙소 수정 요청 예시: PUT /api/lodgings/1 Body(JSON): { "lodgingName": "수정된 숙소명",
	 * "region": "부산" } 흐름: 1. URL에서 수정할 숙소 번호(lodgingNo)를 받는다. 2. Body에서 수정할 값을
	 * DTO로 받는다. 3.Service에서 기존 데이터를 조회한다. 4. DTO 값 중 null이 아닌 값만 기존 Entity에 반영한다.
	 * 5. 수정된 Entity를 저장한다. 6. 결과를 DTO로 변환해 반환한다.
	 */
	@PatchMapping("/{lodgingNo}")
	public LodgingDTO updateLodging(@PathVariable Long lodgingNo, @RequestBody LodgingDTO lodgingDTO) {
		Lodging updatedLodging = lodgingService.updateLodging(lodgingNo, lodgingDTO);
		return LodgingDTO.fromEntity(updatedLodging);
	}

	/**
	 * 숙소 삭제
	 * 
	 * 요청 예시: DELETE /api/lodgings/1
	 * 
	 * 삭제 성공 시 응답 바디 없이 204 No Content 반환
	 */
	@DeleteMapping("/{lodgingNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteLodging(@PathVariable Long lodgingNo) {
		lodgingService.deleteLodging(lodgingNo);
	}

	/**
	 * 숙소 상세보기 API
	 * 
	 * 요청 예시: GET /api/v1/lodgings/1/detail
	 * 
	 * 흐름: 1. URL에서 lodgingNo를 받는다. 2. Service에서 숙소 기본 정보 + 이미지 목록 + 객실 목록을 조회한다. 3.
	 * 하나의 LodgingDetailDTO로 묶는다. 4. JSON 형태로 반환한다.
	 */
	@GetMapping("/{lodgingNo}/detail") // 상세보기 전용 API
	public LodgingDetailDTO getLodgingDetail(@PathVariable Long lodgingNo) {
		return lodgingService.getLodgingDetail(lodgingNo); // 상세 DTO 반환
	}

}
