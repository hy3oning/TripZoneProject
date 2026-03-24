package com.kh.trip.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
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

}
