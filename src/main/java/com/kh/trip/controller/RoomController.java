package com.kh.trip.controller;

import java.util.List;

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

import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.dto.RoomUpdateDTO;
import com.kh.trip.service.RoomService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/rooms")
@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoomDetailDTO createRoom(@RequestBody RoomCreateDTO createDTO) {
		return roomService.createRoom(createDTO);
	}

	@GetMapping("/lodging/{lodgingNo}")
	public List<RoomSummaryDTO> getRoomsByLodgingNo(@PathVariable Long lodgingNo) {
		return roomService.getRoomsByLodgingNo(lodgingNo);
	}

	@GetMapping("/{roomNo}")
	public RoomDetailDTO getRoomDetail(@PathVariable Long roomNo) {
		return roomService.getRoomDetail(roomNo);
	}

	@PatchMapping("/{roomNo}")
	public RoomDetailDTO updateRoom(@PathVariable Long roomNo, @RequestBody RoomUpdateDTO updateDTO) {
		return roomService.updateRoom(roomNo, updateDTO);
	}

	@DeleteMapping("/{roomNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRoom(@PathVariable Long roomNo) {
		roomService.deleteRoom(roomNo);
	}

	@GetMapping
	public List<RoomSummaryDTO> getAllRooms() {
		return roomService.getAllRooms();
	}

	@GetMapping("/status/{status}")
	public List<RoomSummaryDTO> getRoomsByStatus(@PathVariable RoomStatus status) {
		return roomService.getRoomsByStatus(status);
	}

	@GetMapping("/search")
	public List<RoomSummaryDTO> searchRoomsByName(@RequestParam String keyword) {
		return roomService.searchRoomsByName(keyword);
	}

	@GetMapping("/lodging/{lodgingNo}/status/{status}")
	public List<RoomSummaryDTO> getRoomsByLodgingNoAndStatus(@PathVariable Long lodgingNo,
			@PathVariable RoomStatus status) {
		return roomService.getRoomsByLodgingNoAndStatus(lodgingNo, status);
	}

}
