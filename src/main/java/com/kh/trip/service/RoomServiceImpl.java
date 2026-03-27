package com.kh.trip.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.Room;
import com.kh.trip.domain.RoomImage;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.RoomDTO;
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
	public RoomDTO createRoom(RoomDTO roomDTO) {

		// lodgingNo로 실제 Lodging 엔티티 조회
		Lodging lodging = lodgingRepository.findById(roomDTO.getLodgingNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + roomDTO.getLodgingNo()));

		// 등록 요청 DTO 값을 이용해서 Room 엔티티 생성
		Room room = Room.builder().lodging(lodging) // Lodging 엔티티 세팅
				.roomName(roomDTO.getRoomName()) // 객실명 세팅
				.roomType(roomDTO.getRoomType()) // 객실 유형 세팅
				.roomDescription(roomDTO.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(roomDTO.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(roomDTO.getPricePerNight()) // 1박 가격 세팅
				.roomCount(roomDTO.getRoomCount()) // 객실 수 세팅

				// status가 null이면 기본값 AVAILABLE 사용
				.status(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.AVAILABLE).build();

		// DB에 객실 저장
		Room savedRoom = roomRepository.save(room);

		// 객실 저장 후 ROOM_IMAGES 테이블에 이미지들도 같이 저장
		saveRoomImages(savedRoom, roomDTO.getImageUrls());

		// 저장된 객실의 이미지 URL 목록을 다시 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(savedRoom.getRoomNo())
				.stream().map(RoomImage::getImageUrl).toList();

		// RoomDTO 하나로
		return toRoomDTO(savedRoom, imageUrls);
	}

	@Override
	public List<RoomDTO> getRoomsByLodgingNo(Long lodgingNo) {
		// 숙소 번호에 해당하는 객실 목록 조회
		return roomRepository.findByLodging_LodgingNo(lodgingNo).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl).toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	@Override
	public RoomDTO getRoomDetail(Long roomNo) {
		// 객실 번호로 상세조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 상세 조회 시 객실 이미지 목록도 함께 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo).stream()
				.map(RoomImage::getImageUrl).toList();

		// RoomDTO 하나로
		return toRoomDTO(room, imageUrls);
	}

	@Override
	@Transactional
	public RoomDTO updateRoom(Long roomNo, RoomDTO roomDTO) {
		// 수정할 객실 조회
		Room room = roomRepository.findById(roomNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. roomNo=" + roomNo));

		// 숙소 변경 값이 들어온 경우 실제 Lodging 엔티티로 교체
		if (roomDTO.getLodgingNo() != null) {
			Lodging lodging = lodgingRepository.findById(roomDTO.getLodgingNo()).orElseThrow(
					() -> new IllegalArgumentException("존재하지 않는 숙소입니다. lodgingNo=" + roomDTO.getLodgingNo()));
			room.changeLodging(lodging);
		}

		// roomName 값이 들어왔을 때만 수정
		if (roomDTO.getRoomName() != null) {
			room.changeRoomName(roomDTO.getRoomName());
		}

		// roomType 값이 들어왔을 때만 수정
		if (roomDTO.getRoomType() != null) {
			room.changeRoomType(roomDTO.getRoomType());
		}

		// roomDescription 값이 들어왔을 때만 수정
		if (roomDTO.getRoomDescription() != null) {
			room.changeRoomDescription(roomDTO.getRoomDescription());
		}

		// maxGuestCount 값이 들어왔을 때만 수정
		if (roomDTO.getMaxGuestCount() != null) {
			room.changeMaxGuestCount(roomDTO.getMaxGuestCount());
		}

		// pricePerNight 값이 들어왔을 때만 수정
		if (roomDTO.getPricePerNight() != null) {
			room.changePricePerNight(roomDTO.getPricePerNight());
		}

		// roomCount 값이 들어왔을 때만 수정
		if (roomDTO.getRoomCount() != null) {
			room.changeRoomCount(roomDTO.getRoomCount());
		}

		// status 값이 들어왔을 때만 수정
		if (roomDTO.getStatus() != null) {
			room.changeStatus(roomDTO.getStatus());
		}

		// 수정 시 기존 객실 이미지 전부 삭제
		roomImageRepository.deleteByRoom_RoomNo(roomNo);

		// 수정 요청으로 들어온 새 이미지 다시 저장
		saveRoomImages(room, roomDTO.getImageUrls());

		// 수정 후 이미지 목록 다시 조회
		List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(roomNo).stream()
				.map(RoomImage::getImageUrl).toList();

		// RoomDTO 하나로
		return toRoomDTO(room, imageUrls);
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
	public List<RoomDTO> getAllRooms() {
		// Room 테이블의 전체 데이터를 조회
		return roomRepository.findAllByOrderByRoomNoAsc().stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl).toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 상태별 객실 목록 조회
	@Override
	public List<RoomDTO> getRoomsByStatus(RoomStatus status) { // [수정]
		return roomRepository.findByStatusOrderByRoomNoAsc(status).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl).toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 객실명 검색
	@Override
	public List<RoomDTO> searchRoomsByName(String keyword) {
		return roomRepository.findByRoomNameContainingOrderByRoomNoAsc(keyword).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl).toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 특정 숙소 번호 + 상태별 객실 목록 조회
	@Override
	public List<RoomDTO> getRoomsByLodgingNoAndStatus(Long lodgingNo, RoomStatus status) {
		// Room 엔티티가 lodging 객체를 가지므로 Repository 메서드도 연관관계 기준으로 바뀌어야 함
		return roomRepository.findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(lodgingNo, status).stream().map(room -> {
			List<String> imageUrls = roomImageRepository.findByRoom_RoomNoOrderBySortOrderAsc(room.getRoomNo()).stream()
					.map(RoomImage::getImageUrl).toList();

			return toRoomDTO(room, imageUrls);
		}).toList();
	}

	// 객실 이미지 저장 공통 메서드
	private void saveRoomImages(Room room, List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return;
		}

		IntStream.range(0, imageUrls.size()).mapToObj(
				index -> RoomImage.builder().room(room).imageUrl(imageUrls.get(index)).sortOrder(index + 1).build())
				.forEach(roomImageRepository::save);
	}

	// Room 엔티티를 RoomDTO로 변환하는 메서드
	private RoomDTO toRoomDTO(Room room, List<String> imageUrls) {
		return RoomDTO.builder().roomNo(room.getRoomNo()) // 객실 번호
				.lodgingNo(room.getLodging().getLodgingNo()) // 어떤 숙소에 속한 객실인지 보여주기 위해 추가
				.roomName(room.getRoomName()) // 객실 이름
				.roomType(room.getRoomType()) // 객실 타입
				.roomDescription(room.getRoomDescription()) // 객실 설명
				.maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원
				.pricePerNight(room.getPricePerNight()) // 1박 가격
				.roomCount(room.getRoomCount()) // 객실 개수
				.status(room.getStatus()) // 객실 상태
				.imageUrls(imageUrls) // 이미지 URL 목록
				.build();
	}
}