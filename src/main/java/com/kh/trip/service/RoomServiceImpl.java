package com.kh.trip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Room;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
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

				// 숙소 번호 세팅
				.lodgingNo(createDTO.getLodgingNo())

				// 객실명 세팅
				.roomName(createDTO.getRoomName())

				// 객실 유형 세팅
				.roomType(createDTO.getRoomType())

				// 객실 설명 세팅
				.roomDescription(createDTO.getRoomDescription())

				// 최대 수용 인원 세팅
				.maxGuestCount(createDTO.getMaxGuestCount())

				// 1박 가격 세팅
				.pricePerNight(createDTO.getPricePerNight())

				// 객실 수 세팅
				.roomCount(createDTO.getRoomCount())

				// status가 null이면 기본값 AVAILABLE 사용
				.status(createDTO.getStatus() != null ? createDTO.getStatus() : RoomStatus.AVAILABLE)

				// Room 엔티티 생성 완료
				.build();

		// DB에 객실 저장
		Room savedRoom = roomRepository.save(room);

		// createRoom 내부에서 직접 DTO builder를 쓰지 않고 밖에 뺀 변환 메서드 호출
		return toDetailDTO(savedRoom);
	}

	// Room 엔티티를 RoomDetailDTO로 변환하는 메서드
	private RoomDetailDTO toDetailDTO(Room room) {

		// 상세 조회용 DTO 생성
		return RoomDetailDTO.builder()

				// 객실 번호 세팅
				.roomNo(room.getRoomNo())

				// 숙소 번호 세팅
				.lodgingNo(room.getLodgingNo())

				// 객실명 세팅
				.roomName(room.getRoomName())

				// 객실 유형 세팅
				.roomType(room.getRoomType())

				// 객실 설명 세팅
				.roomDescription(room.getRoomDescription())

				// 최대 수용 인원 세팅
				.maxGuestCount(room.getMaxGuestCount())

				// 1박 가격 세팅
				.pricePerNight(room.getPricePerNight())

				// 객실 수 세팅
				.roomCount(room.getRoomCount())

				// 상태 세팅
				.status(room.getStatus())

				// RoomDetailDTO 생성 완료
				.build();
	}

}
