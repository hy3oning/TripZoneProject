package com.kh.trip.service;

import java.util.List;

import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomDTO;

// 객실 관련 비즈니스 기능 명세 인터페이스
public interface RoomService {

	// 객실 등록
	RoomDTO createRoom(RoomDTO createDTO);

	// 특정 숙소에 속한 객실 목록 조회
	List<RoomDTO> getRoomsByLodgingNo(Long lodgingNo);

	// 객실 상세 조회
	RoomDTO getRoomDetail(Long roomNo);

	// 판매자 관리용 객실 조회
	// 상태와 상관없이 관리 목적의 데이터를 가져올 때 사용
	RoomDTO getRoomForManagement(Long roomNo);

	// 객실 수정
	RoomDTO updateRoom(Long roomNo, RoomDTO updateDTO);

	// 객실 삭제
	// 실제 물리 삭제 대신 상태 변경 방식으로 처리
	void deleteRoom(Long roomNo);

	// 전체 객실 목록 조회
	List<RoomDTO> getAllRooms();

	// 상태별 객실 목록 조회
	List<RoomDTO> getRoomsByStatus(RoomStatus status);

	// 객실명 검색
	List<RoomDTO> searchRoomsByName(String keyword);

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	List<RoomDTO> getRoomsByLodgingNoAndStatus(Long lodgingNo, RoomStatus status);
}