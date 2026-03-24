package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.dto.RoomUpdateDTO;

public interface RoomService {

	// 객실 등록 기능
	RoomDetailDTO createRoom(RoomCreateDTO createDTO);
	
	 // 특정 숙소 번호에 해당하는 객실 목록 조회 기능
    List<RoomSummaryDTO> getRoomsByLodgingNo(Long lodgingNo);

    // 객실 상세 조회 기능
    RoomDetailDTO getRoomDetail(Long roomNo);

    // 객실 수정 기능
    RoomDetailDTO updateRoom(Long roomNo, RoomUpdateDTO updateDTO);

    // 객실 삭제 기능
    void deleteRoom(Long roomNo);

}
