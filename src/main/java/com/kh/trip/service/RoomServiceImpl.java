package com.kh.trip.service;

import java.util.List;
import java.util.stream.IntStream; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Room;
import com.kh.trip.domain.RoomImage; 
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
import com.kh.trip.dto.RoomImageDTO; 
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.dto.RoomUpdateDTO;
import com.kh.trip.repository.RoomImageRepository;
import com.kh.trip.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;
	private final RoomImageRepository roomImageRepository; 

	// 객실 등록 기능
	@Override
	@Transactional
	public RoomDetailDTO createRoom(RoomCreateDTO createDTO) {

		// 등록 요청 DTO 값을 이용해서 Room 엔티티 생성
		Room room = Room.builder().lodgingNo(createDTO.getLodgingNo()) // 숙소 번호 세팅
				.roomName(createDTO.getRoomName()) // 객실명 세팅
				.roomType(createDTO.getRoomType()) // 객실 유형 세팅
				.roomDescription(createDTO.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(createDTO.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(createDTO.getPricePerNight()) // 1박 가격 세팅
				.roomCount(createDTO.getRoomCount()) // 객실 수 세팅

				// status가 null이면 기본값 AVAILABLE 사용
				.status(createDTO.getStatus() != null ? createDTO.getStatus() : RoomStatus.AVAILABLE).build();

		// DB에 객실 저장
		Room savedRoom = roomRepository.save(room);

		// 객실 저장 후 ROOM_IMAGES 테이블에 이미지들도 같이 저장
		saveRoomImages(savedRoom, createDTO.getImageUrls());

		// 저장된 객실의 이미지 목록을 다시 조회
		List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(savedRoom.getRoomNo())
				.stream()
				.map(this::toRoomImageDTO)
				.toList();
		// createRoom 내부에서 직접 DTO builder를 쓰지 않고 밖에 뺀 변환 메서드 호출
		return toDetailDTO(savedRoom, imageDTOs); // 이미지 목록까지 포함해서 반환
	}

	@Override
	public List<RoomSummaryDTO> getRoomsByLodgingNo(Long lodgingNo) {
		// 숙소 번호에 해당하는 객실 목록 조회
		return roomRepository.findByLodgingNo(lodgingNo).stream() // Stream으로 변환
				.map(room -> {
					// 각 객실의 이미지 목록도 함께 조회
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();
					return toSummaryDTO(room, imageDTOs); // 이미지 포함 DTO 변환
				})
				.toList(); // List로 변환해서 반환
	}

	@Override
	public RoomDetailDTO getRoomDetail(Long roomNo) {
		// 객실 번호로 상세조회
		Room room = roomRepository.findById(roomNo)
				// 해당 객실이 없으면 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));
		// 상세 조회 시 객실 이미지 목록도 함께 조회
		List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo)
				.stream()
				.map(this::toRoomImageDTO)
				.toList();
		// 밖에 뺀 상세 DTO 변환 메서드 호출
		return toDetailDTO(room, imageDTOs); // 이미지 목록 포함해서 반환
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

		// 수정 시 기존 객실 이미지 전부 삭제
		roomImageRepository.deleteByRoom_RoomNo(roomNo);

		// 수정 요청으로 들어온 새 이미지 다시 저장
		saveRoomImages(room, updateDTO.getImageUrls());

		// 수정 후 이미지 목록 다시 조회
		List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo)
				.stream()
				.map(this::toRoomImageDTO)
				.toList();
		// 수정 결과도 밖에 뺀 변환 메서드 호출
		return toDetailDTO(room, imageDTOs); // 이미지 포함 DTO 반환
	}

	@Override
	@Transactional
	public void deleteRoom(Long roomNo) {
		// 삭제할 객실 조회
		Room room = roomRepository.findById(roomNo)
				// 해당 객실이 없으면 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));
		// 객실 삭제 전에 ROOM_IMAGES 테이블의 연결 이미지 먼저 삭제
		roomImageRepository.deleteByRoom_RoomNo(roomNo);
		// 객실 삭제
		roomRepository.delete(room);
	}

	// 전체 객실 목록 조회
	@Override
	public List<RoomSummaryDTO> getAllRooms() {
		// Room 테이블의 전체 데이터를 조회
		return roomRepository.findAllByOrderByRoomNoAsc().stream() // 각 Room 엔티티를 RoomSummaryDTO로 변환
				.map(room -> {
					// 전체 조회에서도 각 객실 이미지 목록 포함
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();
					return toSummaryDTO(room, imageDTOs); // 이미지 포함 DTO 변환
				})
				.toList(); // List로 만들어서 반환
	}

	// 상태별 객실 목록 조회
	@Override
	public List<RoomSummaryDTO> getRoomsByStatus(RoomStatus status) {
		// status 값에 해당하는 객실들을 roomNo 오름차순으로 조회
		return roomRepository.findByStatusOrderByRoomNoAsc(status).stream()
				.map(room -> {
					// 상태별 조회에서도 이미지 목록 포함
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();
					return toSummaryDTO(room, imageDTOs); // 이미지 포함 DTO 변환
				})
				.toList();
	}

	// 객실명 검색
	@Override
	public List<RoomSummaryDTO> searchRoomsByName(String keyword) {
		// roomName에 keyword가 포함된 객실들을 roomNo 오름차순으로 조회
		return roomRepository.findByRoomNameContainingOrderByRoomNoAsc(keyword).stream()
				.map(room -> {
					// 검색 결과에도 이미지 목록 포함
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();
					return toSummaryDTO(room, imageDTOs); // 이미지 포함 DTO 변환
				})
				.toList();
	}

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	@Override
	public List<RoomSummaryDTO> getRoomsByLodgingNoAndStatus(Long lodgingNo, RoomStatus status) {
		// lodgingNo와 status가 모두 일치하는 객실들을 roomNo 오름차순으로 조회
		return roomRepository.findByLodgingNoAndStatusOrderByRoomNoAsc(lodgingNo, status).stream()
				.map(room -> {
					// 숙소 번호 + 상태 조회 결과에도 이미지 목록 포함
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();
					return toSummaryDTO(room, imageDTOs); // 이미지 포함 DTO 변환
				})
				.toList();
	}

	// RoomImage 엔티티를 RoomImageDTO로 변환하는 메서드
	private RoomImageDTO toRoomImageDTO(RoomImage roomImage) {
		// 객실 이미지 DTO 생성
		return RoomImageDTO.builder()
				.roomImageNo(roomImage.getRoomImageNo()) // 객실 이미지 번호 세팅
				.imageUrl(roomImage.getImageUrl()) // 이미지 주소 세팅
				.sortOrder(roomImage.getSortOrder()) // 이미지 정렬 순서 세팅
				.regDate(roomImage.getRegDate()) // 이미지 등록일 세팅
				.build(); // RoomImageDTO 생성 완료
	}

	// 객실 이미지 저장 공통 메서드
	// Room 엔티티를 직접 받는 구조로 변경
	private void saveRoomImages(Room room, List<String> imageUrls) {
		// 이미지 목록이 없으면 저장하지 않음
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}
		// 이미지 순서를 1부터 부여해서 저장
		IntStream.range(0, imageUrls.size())
				.mapToObj(index -> RoomImage.builder()
						.room(room) // Room 엔티티를 세팅
						.imageUrl(imageUrls.get(index)) // 이미지 주소 세팅
						.sortOrder(index + 1) // 정렬 순서 세팅
						.build())
				.forEach(roomImageRepository::save);
	}

	// Room 엔티티를 RoomDetailDTO로 변환하는 메서드
	private RoomDetailDTO toDetailDTO(Room room, List<RoomImageDTO> images) {
		// 상세 조회용 DTO 생성
		return RoomDetailDTO.builder().roomNo(room.getRoomNo()) // 객실 번호 세팅
				.lodgingNo(room.getLodgingNo()) // 숙소 번호 세팅
				.roomName(room.getRoomName()) // 객실명 세팅
				.roomType(room.getRoomType()) // 객실 유형 세팅
				.roomDescription(room.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(room.getPricePerNight()) // 1박 가격 세팅
				.roomCount(room.getRoomCount()) // 객실 수 세팅
				.status(room.getStatus()) // 상태 세팅
				.images(images) // 객실 이미지 목록 세팅
				.build(); // RoomDetailDTO 생성 완료
	}

	// Room 엔티티를 RoomSummaryDTO로 변환하는 메서드
	private RoomSummaryDTO toSummaryDTO(Room room, List<RoomImageDTO> images) {
		return RoomSummaryDTO.builder() // 목록 조회용 DTO 생성
				.roomNo(room.getRoomNo()) // 객실 번호 세팅
				.roomName(room.getRoomName()) // 객실명 세팅
				.roomType(room.getRoomType()) // 객실 유형 세팅
				.roomDescription(room.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(room.getPricePerNight()) // 1박 가격 세팅
				.roomCount(room.getRoomCount()) // 객실 수 세팅
				.status(room.getStatus()) // 상태 세팅
				.images(images) // 객실 이미지 목록 세팅
				.build(); // RoomSummaryDTO 생성 완료
	}

}