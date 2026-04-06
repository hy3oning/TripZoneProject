package com.kh.trip.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.Room;
import com.kh.trip.domain.RoomImage;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomDTO;
import com.kh.trip.repository.LodgingRepository;
import com.kh.trip.repository.RoomImageRepository;
import com.kh.trip.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

// RoomService 인터페이스의 실제 구현체
// 객실 등록, 조회, 수정, 삭제 등의 비즈니스 로직을 담당한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

	// 객실 테이블 처리 Repository
	private final RoomRepository roomRepository;

	// 객실 이미지 테이블 처리 Repository
	private final RoomImageRepository roomImageRepository;

	// 숙소 존재 여부 및 상태 확인용 Repository
	private final LodgingRepository lodgingRepository;

	// 객실 등록
	@Override
	@Transactional
	public RoomDTO createRoom(RoomDTO roomDTO) {

		// 1. 등록 요청값 기본 검증
		validateRoomDTOForCreate(roomDTO);

		// 2. lodgingNo로 실제 숙소 엔티티 조회
		Lodging lodging = lodgingRepository.findById(roomDTO.getLodgingNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + roomDTO.getLodgingNo()));

		// 3. 비활성 숙소에는 객실 등록 불가
		if (lodging.getStatus() != LodgingStatus.ACTIVE) {
			throw new IllegalArgumentException("비활성화된 숙소에는 객실을 등록할 수 없습니다.");
		}

		// 4. DTO 값을 기반으로 Room 엔티티 생성
		Room room = Room.builder()
				.lodging(lodging)
				.roomName(roomDTO.getRoomName())
				.roomType(roomDTO.getRoomType())
				.roomDescription(roomDTO.getRoomDescription())
				.maxGuestCount(roomDTO.getMaxGuestCount())
				.pricePerNight(roomDTO.getPricePerNight())
				.roomCount(roomDTO.getRoomCount())

				// status가 없으면 기본값 AVAILABLE 사용
				.status(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.AVAILABLE)
				.build();

		// 5. 객실 저장
		Room savedRoom = roomRepository.save(room);

		// 6. 객실 이미지가 있으면 ROOM_IMAGES 테이블에 함께 저장
		saveRoomImages(savedRoom, roomDTO.getImageUrls());

		// 7. 저장 후 이미지 목록 다시 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(savedRoom.getRoomNo())
				.stream()
				.map(RoomImage::getImageUrl)
				.toList();

		// 8. 최종 DTO 반환
		return toRoomDTO(savedRoom, imageUrls);
	}

	// 특정 숙소의 객실 목록 조회
	@Override
	public List<RoomDTO> getRoomsByLodgingNo(Long lodgingNo) {

		// 일반 사용자 화면에서는 예약 가능한 객실만 조회
		return roomRepository.findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(lodgingNo, RoomStatus.AVAILABLE)
				.stream()
				.map(room -> {
					List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo())
							.stream()
							.map(RoomImage::getImageUrl)
							.toList();

					return toRoomDTO(room, imageUrls);
				}).toList();
	}

	// 객실 상세 조회
	@Override
	public RoomDTO getRoomDetail(Long roomNo) {

		// 1. 객실 번호로 조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 2. 일반 상세조회에서는 AVAILABLE 상태 객실만 허용
		if (room.getStatus() != RoomStatus.AVAILABLE) {
			throw new IllegalArgumentException("사용할 수 없는 객실입니다. roomNo=" + roomNo);
		}

		// 3. 객실 이미지 목록도 함께 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo).stream()
				.map(RoomImage::getImageUrl)
				.toList();

		return toRoomDTO(room, imageUrls);
	}

	// 판매자 관리용 객실 조회
	@Override
	public RoomDTO getRoomForManagement(Long roomNo) {

		// 관리 화면에서는 상태와 상관없이 객실 자체를 조회해야 하므로
		// AVAILABLE 여부를 검사하지 않는다.
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo).stream()
				.map(RoomImage::getImageUrl)
				.toList();

		return toRoomDTO(room, imageUrls);
	}

	// 객실 수정
	@Override
	@Transactional
	public RoomDTO updateRoom(Long roomNo, RoomDTO roomDTO) {

		// 1. 수정 대상 객실 조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 2. 숙소 변경 값이 들어온 경우 실제 숙소 엔티티로 교체
		if (roomDTO.getLodgingNo() != null) {
			Lodging lodging = lodgingRepository.findById(roomDTO.getLodgingNo())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + roomDTO.getLodgingNo()));

			// 비활성 숙소로 객실 이동 불가
			if (lodging.getStatus() != LodgingStatus.ACTIVE) {
				throw new IllegalArgumentException("비활성화된 숙소로 객실을 이동할 수 없습니다.");
			}

			room.changeLodging(lodging);
		}

		// 3. 객실명 수정
		if (roomDTO.getRoomName() != null) {
			if (roomDTO.getRoomName().isBlank()) {
				throw new IllegalArgumentException("객실명은 비워둘 수 없습니다.");
			}
			room.changeRoomName(roomDTO.getRoomName());
		}

		// 4. 객실 유형 수정
		if (roomDTO.getRoomType() != null) {
			if (roomDTO.getRoomType().isBlank()) {
				throw new IllegalArgumentException("객실 유형은 비워둘 수 없습니다.");
			}
			room.changeRoomType(roomDTO.getRoomType());
		}

		// 5. 객실 설명 수정
		if (roomDTO.getRoomDescription() != null) {
			room.changeRoomDescription(roomDTO.getRoomDescription());
		}

		// 6. 최대 수용 인원 수정
		if (roomDTO.getMaxGuestCount() != null) {
			if (roomDTO.getMaxGuestCount() < 1) {
				throw new IllegalArgumentException("최대 수용 인원은 1명 이상이어야 합니다.");
			}
			room.changeMaxGuestCount(roomDTO.getMaxGuestCount());
		}

		// 7. 1박 가격 수정
		if (roomDTO.getPricePerNight() != null) {
			if (roomDTO.getPricePerNight() < 0) {
				throw new IllegalArgumentException("1박 가격은 0원 이상이어야 합니다.");
			}
			room.changePricePerNight(roomDTO.getPricePerNight());
		}

		// 8. 객실 수 수정
		if (roomDTO.getRoomCount() != null) {
			if (roomDTO.getRoomCount() < 1) {
				throw new IllegalArgumentException("객실 수는 1개 이상이어야 합니다.");
			}
			room.changeRoomCount(roomDTO.getRoomCount());
		}

		// 9. 상태 수정
		if (roomDTO.getStatus() != null) {
			room.changeStatus(roomDTO.getStatus());
		}

		// 10. 이미지 목록이 들어오면 기존 이미지 전체 삭제 후 새 이미지 저장
		if (roomDTO.getImageUrls() != null) {
			roomImageRepository.deleteByRoom_RoomNo(roomNo);
			saveRoomImages(room, roomDTO.getImageUrls());
		}

		// 11. 수정 후 이미지 목록 다시 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo).stream()
				.map(RoomImage::getImageUrl)
				.toList();

		return toRoomDTO(room, imageUrls);
	}

	// 객실 삭제
	@Override
	@Transactional
	public void deleteRoom(Long roomNo) {

		// 삭제 대상 객실 조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 실제 물리 삭제 대신 객실 상태만 UNAVAILABLE로 변경
		room.changeStatus(RoomStatus.UNAVAILABLE);
	}

	// 전체 객실 목록 조회
	@Override
	public List<RoomDTO> getAllRooms() {
		return roomRepository.findAllByOrderByRoomNoAsc().stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl)
					.toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 상태별 객실 목록 조회
	@Override
	public List<RoomDTO> getRoomsByStatus(RoomStatus status) {
		return roomRepository.findByStatusOrderByRoomNoAsc(status).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl)
					.toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 객실명 검색
	@Override
	public List<RoomDTO> searchRoomsByName(String keyword) {
		return roomRepository.findByRoomNameContainingOrderByRoomNoAsc(keyword).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl)
					.toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	@Override
	public List<RoomDTO> getRoomsByLodgingNoAndStatus(Long lodgingNo, RoomStatus status) {
		return roomRepository.findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(lodgingNo, status).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl)
					.toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 객실 이미지 저장 공통 메서드
	private void saveRoomImages(Room room, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		// index + 1을 sortOrder로 사용해서 이미지 순서를 저장
		IntStream.range(0, imageUrls.size())
				.mapToObj(index -> RoomImage.builder()
						.room(room)
						.imageUrl(imageUrls.get(index))
						.sortOrder(index + 1)
						.build())
				.forEach(roomImageRepository::save);
	}

	// 객실 등록 시 기본 유효성 검사
	private void validateRoomDTOForCreate(RoomDTO roomDTO) {
		if (roomDTO == null) {
			throw new IllegalArgumentException("객실 정보가 없습니다.");
		}

		if (roomDTO.getLodgingNo() == null) {
			throw new IllegalArgumentException("숙소 번호는 필수입니다.");
		}

		if (roomDTO.getRoomName() == null || roomDTO.getRoomName().isBlank()) {
			throw new IllegalArgumentException("객실명은 필수입니다.");
		}

		if (roomDTO.getRoomType() == null || roomDTO.getRoomType().isBlank()) {
			throw new IllegalArgumentException("객실 유형은 필수입니다.");
		}

		if (roomDTO.getMaxGuestCount() == null || roomDTO.getMaxGuestCount() < 1) {
			throw new IllegalArgumentException("최대 수용 인원은 1명 이상이어야 합니다.");
		}

		if (roomDTO.getPricePerNight() == null || roomDTO.getPricePerNight() < 0) {
			throw new IllegalArgumentException("1박 가격은 0원 이상이어야 합니다.");
		}

		if (roomDTO.getRoomCount() == null || roomDTO.getRoomCount() < 1) {
			throw new IllegalArgumentException("객실 수는 1개 이상이어야 합니다.");
		}
	}

	// Room 엔티티를 RoomDTO로 변환하는 메서드
	private RoomDTO toRoomDTO(Room room, List<String> imageUrls) {
		return RoomDTO.builder()
				.roomNo(room.getRoomNo())
				.lodgingNo(room.getLodging().getLodgingNo())
				.roomName(room.getRoomName())
				.roomType(room.getRoomType())
				.roomDescription(room.getRoomDescription())
				.maxGuestCount(room.getMaxGuestCount())
				.pricePerNight(room.getPricePerNight())
				.roomCount(room.getRoomCount())
				.status(room.getStatus())
				.imageUrls(imageUrls)
				.build();
	}
}