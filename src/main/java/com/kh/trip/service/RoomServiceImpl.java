package com.kh.trip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Room;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.dto.RoomUpdateDTO;
import com.kh.trip.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;

	@Override
	@Transactional
	// 객실 등록 기능
	public RoomDetailDTO createRoom(RoomCreateDTO createDTO) {

		// 등록 요청 DTO 값을 이용해서 Room 엔티티 생성
		Room room = Room.builder()
				.lodgingNo(createDTO.getLodgingNo()) // 숙소 번호 세팅
				.roomName(createDTO.getRoomName()) // 객실명 세팅
				.roomType(createDTO.getRoomType()) // 객실 유형 세팅
				.roomDescription(createDTO.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(createDTO.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(createDTO.getPricePerNight()) // 1박 가격 세팅
				.roomCount(createDTO.getRoomCount()) // 객실 수 세팅
				
				// status가 null이면 기본값 AVAILABLE 사용
				.status(createDTO.getStatus() != null ? createDTO.getStatus() : RoomStatus.AVAILABLE) 
				.build(); // Room 엔티티 생성 완료

		// DB에 객실 저장
		Room savedRoom = roomRepository.save(room);

		// createRoom 내부에서 직접 DTO builder를 쓰지 않고 밖에 뺀 변환 메서드 호출
		return toDetailDTO(savedRoom);
	}

	@Override
	public List<RoomSummaryDTO> getRoomsByLodgingNo(Long lodgingNo) {

		// 숙소 번호에 해당하는 객실 목록 조회
		return roomRepository.findByLodgingNo(lodgingNo)
				.stream() // Stream으로 변환
				.map(this::toSummaryDTO) // 밖에 뺀 변환 메서드 호출
				.toList(); // List로 변환해서 반환
	}

	@Override
	public RoomDetailDTO getRoomDetail(Long roomNo) {

		// 객실 번호로 상세조회
		Room room = roomRepository.findById(roomNo)

		// 해당 객실이 없으면 예외 발생
		.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 밖에 뺀 상세 DTO 변환 메서드 호출
		return toDetailDTO(room);
	}

	@Override
	@Transactional
	public RoomDetailDTO updateRoom(Long roomNo, RoomUpdateDTO updateDTO) {

		// 수정할 객실 조회
		Room room = roomRepository.findById(roomNo)

				// 해당 객실이 없으면 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// roomName 값이 들어왔을 때만 수정
		if (updateDTO.getRoomName() != null) {
			room.changeRoomName(updateDTO.getRoomName());
		}

		// roomType 값이 들어왔을 때만 수정
		if (updateDTO.getRoomType() != null) {
			room.changeRoomType(updateDTO.getRoomType());
		}

		// roomDescription 값이 들어왔을 때만 수정
		if (updateDTO.getRoomDescription() != null) {
			room.changeRoomDescription(updateDTO.getRoomDescription());
		}

		// maxGuestCount 값이 들어왔을 때만 수정
		if (updateDTO.getMaxGuestCount() != null) {
			room.changeMaxGuestCount(updateDTO.getMaxGuestCount());
		}

		// pricePerNight 값이 들어왔을 때만 수정
		if (updateDTO.getPricePerNight() != null) {
			room.changePricePerNight(updateDTO.getPricePerNight());
		}

		// roomCount 값이 들어왔을 때만 수정
		if (updateDTO.getRoomCount() != null) {
			room.changeRoomCount(updateDTO.getRoomCount());
		}

		// status 값이 들어왔을 때만 수정
		if (updateDTO.getStatus() != null) {
			room.changeStatus(updateDTO.getStatus());
		}

		// 수정 결과도 밖에 뺀 변환 메서드 호출
		return toDetailDTO(room);
	}

	@Override
	@Transactional
	public void deleteRoom(Long roomNo) {
		// 삭제할 객실 조회
		Room room = roomRepository.findById(roomNo)
				// 해당 객실이 없으면 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));
		// 객실 삭제
		roomRepository.delete(room);
	}

	// Room 엔티티를 RoomDetailDTO로 변환하는 메서드
	private RoomDetailDTO toDetailDTO(Room room) {

	    // 상세 조회용 DTO 생성
	    return RoomDetailDTO.builder()
	            .roomNo(room.getRoomNo()) // 객실 번호 세팅
	            .lodgingNo(room.getLodgingNo()) // 숙소 번호 세팅
	            .roomName(room.getRoomName()) // 객실명 세팅
	            .roomType(room.getRoomType()) // 객실 유형 세팅
	            .roomDescription(room.getRoomDescription()) // 객실 설명 세팅
	            .maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원 세팅
	            .pricePerNight(room.getPricePerNight()) // 1박 가격 세팅
	            .roomCount(room.getRoomCount()) // 객실 수 세팅
	            .status(room.getStatus()) // 상태 세팅
	            .build(); // RoomDetailDTO 생성 완료
	}

	// Room 엔티티를 RoomSummaryDTO로 변환하는 메서드
	private RoomSummaryDTO toSummaryDTO(Room room) {
		return RoomSummaryDTO.builder() // 목록 조회용 DTO 생성
				.roomNo(room.getRoomNo()) // 객실 번호 세팅
				.roomName(room.getRoomName()) // 객실명 세팅
				.roomType(room.getRoomType()) // 객실 유형 세팅
				.roomDescription(room.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(room.getPricePerNight()) // 1박 가격 세팅
				.roomCount(room.getRoomCount()) // 객실 수 세팅
				.status(room.getStatus()) // 상태 세팅
				.build(); // RoomSummaryDTO 생성 완료

	}

}
