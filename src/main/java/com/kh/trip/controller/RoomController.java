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
import com.kh.trip.dto.RoomDTO; 
import com.kh.trip.service.RoomService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/rooms")
@RestController
@RequiredArgsConstructor
public class RoomController {

	private final RoomService roomService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoomDTO createRoom(@RequestBody RoomDTO roomDTO) { 
		return roomService.createRoom(roomDTO); 
	}

	@GetMapping("/lodging/{lodgingNo}")
	public List<RoomDTO> getRoomsByLodgingNo(@PathVariable Long lodgingNo) { 
		return roomService.getRoomsByLodgingNo(lodgingNo); 
	}

	@GetMapping("/{roomNo}")
	public RoomDTO getRoomDetail(@PathVariable Long roomNo) { 
		return roomService.getRoomDetail(roomNo); 
	}

	@PatchMapping("/{roomNo}")
	public RoomDTO updateRoom(@PathVariable Long roomNo, @RequestBody RoomDTO roomDTO) { 
		return roomService.updateRoom(roomNo, roomDTO); 
	}

	@DeleteMapping("/{roomNo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRoom(@PathVariable Long roomNo) {
		roomService.deleteRoom(roomNo);
	}

	@GetMapping
	public List<RoomDTO> getAllRooms() { 
		return roomService.getAllRooms(); 
	}

	@GetMapping("/status/{status}")
	public List<RoomDTO> getRoomsByStatus(@PathVariable RoomStatus status) { 
		return roomService.getRoomsByStatus(status); 
	}

	@GetMapping("/search")
	public List<RoomDTO> searchRoomsByName(@RequestParam String keyword) {
		return roomService.searchRoomsByName(keyword); 
	}

	@GetMapping("/lodging/{lodgingNo}/status/{status}")
	public List<RoomDTO> getRoomsByLodgingNoAndStatus(@PathVariable Long lodgingNo,
			@PathVariable RoomStatus status) { 
		return roomService.getRoomsByLodgingNoAndStatus(lodgingNo, status); 
	}
}