package com.kh.trip.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging; 
import com.kh.trip.domain.Room;
import com.kh.trip.domain.RoomImage;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomCreateDTO;
import com.kh.trip.dto.RoomDetailDTO;
import com.kh.trip.dto.RoomImageDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.dto.RoomUpdateDTO;
import com.kh.trip.repository.LodgingRepository; 
import com.kh.trip.repository.RoomImageRepository;
import com.kh.trip.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

	private final RoomRepository roomRepository;
	private final RoomImageRepository roomImageRepository;
	private final LodgingRepository lodgingRepository; 

	// 객실 등록 기능
	@Override
	@Transactional
	public RoomDetailDTO createRoom(RoomCreateDTO createDTO) {

		// lodgingNo로 실제 Lodging 엔티티 조회
		Lodging lodging = lodgingRepository.findById(createDTO.getLodgingNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + createDTO.getLodgingNo()));

		// 등록 요청 DTO 값을 이용해서 Room 엔티티 생성
		Room room = Room.builder()
				.lodging(lodging) // Lodging 엔티티 세팅
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
		return roomRepository.findByLodging_LodgingNo(lodgingNo).stream()
				.map(room -> {
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();

					return toSummaryDTO(room, imageDTOs);
				})
				.toList();
	}

	@Override
	public RoomDetailDTO getRoomDetail(Long roomNo) {
		// 객실 번호로 상세조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 상세 조회 시 객실 이미지 목록도 함께 조회
		List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo)
				.stream()
				.map(this::toRoomImageDTO)
				.toList();

		// 밖에 뺀 상세 DTO 변환 메서드 호출
		return toDetailDTO(room, imageDTOs);
	}

	@Override
	@Transactional
	public RoomDetailDTO updateRoom(Long roomNo, RoomUpdateDTO updateDTO) {
		// 수정할 객실 조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 숙소 변경 값이 들어온 경우 실제 Lodging 엔티티로 교체
		if (updateDTO.getLodgingNo() != null) {
			Lodging lodging = lodgingRepository.findById(updateDTO.getLodgingNo())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + updateDTO.getLodgingNo()));
			room.changeLodging(lodging);
		}

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
		return toDetailDTO(room, imageDTOs);
	}

	@Override
	@Transactional
	public void deleteRoom(Long roomNo) {
		// 삭제할 객실 조회
		Room room = roomRepository.findById(roomNo)
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
		return roomRepository.findAllByOrderByRoomNoAsc().stream()
				.map(room -> {
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();

					return toSummaryDTO(room, imageDTOs);
				})
				.toList();
	}

	// 상태별 객실 목록 조회
	@Override
	public List<RoomSummaryDTO> getRoomsByStatus(RoomStatus status) {
		return roomRepository.findByStatusOrderByRoomNoAsc(status).stream()
				.map(room -> {
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();

					return toSummaryDTO(room, imageDTOs);
				})
				.toList();
	}

	// 객실명 검색
	@Override
	public List<RoomSummaryDTO> searchRoomsByName(String keyword) {
		return roomRepository.findByRoomNameContainingOrderByRoomNoAsc(keyword).stream()
				.map(room -> {
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();

					return toSummaryDTO(room, imageDTOs);
				})
				.toList();
	}

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	@Override
	public List<RoomSummaryDTO> getRoomsByLodgingNoAndStatus(Long lodgingNo, RoomStatus status) {
		// Room 엔티티가 lodging 객체를 가지므로 Repository 메서드도 연관관계 기준으로 바뀌어야 함
		return roomRepository.findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(lodgingNo, status).stream()
				.map(room -> {
					List<RoomImageDTO> imageDTOs = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(this::toRoomImageDTO)
							.toList();

					return toSummaryDTO(room, imageDTOs);
				})
				.toList();
	}

	// RoomImage 엔티티를 RoomImageDTO로 변환하는 메서드
	private RoomImageDTO toRoomImageDTO(RoomImage roomImage) {
		return RoomImageDTO.builder()
				.roomImageNo(roomImage.getRoomImageNo())
				.imageUrl(roomImage.getImageUrl())
				.sortOrder(roomImage.getSortOrder())
				.regDate(roomImage.getRegDate())
				.build();
	}

	// 객실 이미지 저장 공통 메서드
	private void saveRoomImages(Room room, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		IntStream.range(0, imageUrls.size())
				.mapToObj(index -> RoomImage.builder()
						.room(room)
						.imageUrl(imageUrls.get(index))
						.sortOrder(index + 1)
						.build())
				.forEach(roomImageRepository::save);
	}

	// Room 엔티티를 RoomDetailDTO로 변환하는 메서드
	private RoomDetailDTO toDetailDTO(Room room, List<RoomImageDTO> images) {
		return RoomDetailDTO.builder()
				.roomNo(room.getRoomNo())
				.lodgingNo(room.getLodging().getLodgingNo()) // Lodging 엔티티에서 숙소 번호 꺼내기
				.roomName(room.getRoomName())
				.roomType(room.getRoomType())
				.roomDescription(room.getRoomDescription())
				.maxGuestCount(room.getMaxGuestCount())
				.pricePerNight(room.getPricePerNight())
				.roomCount(room.getRoomCount())
				.status(room.getStatus())
				.images(images)
				.build();
	}

	// Room 엔티티를 RoomSummaryDTO로 변환하는 메서드
	private RoomSummaryDTO toSummaryDTO(Room room, List<RoomImageDTO> images) {
		return RoomSummaryDTO.builder()
				.roomNo(room.getRoomNo())
				// [추가] 목록에서도 숙소 번호를 내려주기 위해 Lodging 엔티티에서 꺼냄
				.lodgingNo(room.getLodging().getLodgingNo())
				.roomName(room.getRoomName())
				.roomType(room.getRoomType())
				.roomDescription(room.getRoomDescription())
				.maxGuestCount(room.getMaxGuestCount())
				.pricePerNight(room.getPricePerNight())
				.roomCount(room.getRoomCount())
				.status(room.getStatus())
				.images(images)
				.build();
	}
}