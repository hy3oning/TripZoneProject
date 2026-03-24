package com.kh.trip.service;

import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;

public interface RoomService {

	// 객실 등록 기능
	RoomDetailDTO createRoom(RoomCreateDTO createDTO);

}
