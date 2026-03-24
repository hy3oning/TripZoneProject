package com.kh.trip.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.LodgingImage;
import com.kh.trip.domain.Room;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.RoomStatus;
import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.dto.LodgingDetailDTO;
import com.kh.trip.dto.LodgingImageDTO;
import com.kh.trip.dto.RoomSummaryDTO;
import com.kh.trip.repository.LodgingImageRepository;
import com.kh.trip.repository.LodgingRepository;
import com.kh.trip.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

/**
 * LodgingService 인터페이스를 실제로 구현하는 클래스
 * 
 * @Service - 스프링이 이 클래스를 서비스 빈(Bean)으로 등록해준다.
 * 
 * @RequiredArgsConstructor - final 필드를 매개변수로 받는 생성자를 Lombok이 자동 생성해준다. - 즉, 생성자
 *                          주입 방식으로 LodgingRepository를 자동 주입받는다.
 * 
 * @Transactional - DB 작업을 하나의 트랜잭션 단위로 처리한다. - create / update / delete 같은 변경
 *                작업에서 특히 중요하다.
 */

@Service
@RequiredArgsConstructor
@Transactional

public class LodgingServiceImpl implements LodgingService {

	private final LodgingRepository lodgingRepository;
	private final LodgingImageRepository lodgingImageRepository;
	private final RoomRepository roomRepository;

	// 숙소 등록
	@Override
	public LodgingDTO createLodging(LodgingDTO lodgingDTO) {

		Lodging lodging = toLodgingEntity(lodgingDTO);

		// null 체크
		if (lodging == null) {
			throw new IllegalArgumentException("숙소 정보가 없습니다.");
		}

		// 숙소명 필수 체크
		if (lodging.getLodgingName() == null || lodging.getLodgingName().isBlank()) {
			throw new IllegalArgumentException("숙소명은 필수입니다.");
		}

		// 숙소 유형 필수 체크
		if (lodging.getLodgingType() == null) {
			throw new IllegalArgumentException("숙소 유형은 필수입니다.");
		}

		// 주소 필수 체크
		if (lodging.getAddress() == null || lodging.getAddress().isBlank()) {
			throw new IllegalArgumentException("주소는 필수입니다.");
		}

		// 상태값이 없으면 ACTIVE로 기본 설정
		if (lodging.getStatus() == null) {
			lodging.changeStatus(LodgingStatus.ACTIVE);
		}

		// roomDTO가 없으면 빈 리스트 생성
		List<Room> roomList = lodgingDTO.getRoomDTO() == null ? List.of()
				: lodgingDTO.getRoomDTO().stream().map(this::toRoomEntity).collect(Collectors.toList());

		// 숙소 먼저 저장해서 lodgingNo 생성
		Lodging savedLodging = lodgingRepository.save(lodging);

		if (!roomList.isEmpty()) {
			roomList.forEach(room -> room.changeLodgingNo(savedLodging.getLodgingNo()));
			roomRepository.saveAll(roomList);
		}

		return toLodgingDTO(savedLodging);
	}

	// 숙소 단건 조회
	@Override
	@Transactional(readOnly = true)
	public LodgingDTO getLodging(Long lodgingNo) {
		Lodging lodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("해당 숙소를 찾을 수 없습니다. lodgingNo=" + lodgingNo));

		return toLodgingDTO(lodging);
	}

	// 숙소 전체 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> getAllLodgings() {
		return lodgingRepository.findByStatus(LodgingStatus.ACTIVE).stream().map(this::toLodgingDTO).toList();
	}

	// 지역으로 숙소 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> getLodgingsByRegion(String region) {
		if (region == null || region.isBlank()) {
			throw new IllegalArgumentException("지역 값이 비어 있습니다.");
		}

		return lodgingRepository.findByRegionAndStatus(region, LodgingStatus.ACTIVE).stream().map(this::toLodgingDTO)
				.toList();
	}

	// 숙소명 키워드 검색
	@Override
	@Transactional(readOnly = true)
	public List<LodgingDTO> searchLodgingsByName(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			throw new IllegalArgumentException("검색어가 비어 있습니다.");
		}

		return lodgingRepository.findByLodgingNameContainingAndStatus(keyword, LodgingStatus.ACTIVE).stream()
				.map(this::toLodgingDTO).toList();
	}

	// 숙소 수정
	@Override
	public LodgingDTO updateLodging(Long lodgingNo, LodgingDTO lodgingDTO) {

		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("수정할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));
		applyLodgingUpdate(findLodging, lodgingDTO);
		Lodging updatedLodging = lodgingRepository.save(findLodging);
		return toLodgingDTO(updatedLodging);
	}

	// 숙소 수정값 반영 메서드 domain에 있던 수정 기능을 Service로 옮긴 버전
	// @Setter 방식 대신 엔티티의 change 메서드만 사용하도록 변경
	private void applyLodgingUpdate(Lodging findLodging, LodgingDTO lodgingDTO) {

		if (lodgingDTO.getLodgingName() != null && !lodgingDTO.getLodgingName().isBlank()) {
			findLodging.changeLodgingName(lodgingDTO.getLodgingName());
		}

		if (lodgingDTO.getDescription() != null) {
			findLodging.changeDescription(lodgingDTO.getDescription());
		}

		if (lodgingDTO.getCheckInTime() != null) {
			findLodging.changeCheckInTime(lodgingDTO.getCheckInTime());
		}

		if (lodgingDTO.getCheckOutTime() != null) {
			findLodging.changeCheckOutTime(lodgingDTO.getCheckOutTime());
		}

		if (lodgingDTO.getStatus() != null) {
			findLodging.changeStatus(lodgingDTO.getStatus());
		}
	}

	// 숙소 삭제
	@Override
	public void deleteLodging(Long lodgingNo) {

		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("삭제할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		findLodging.changeStatus(LodgingStatus.INACTIVE);
		lodgingRepository.save(findLodging);

		List<Room> roomList = roomRepository.findByLodgingNo(lodgingNo);
		if (roomList != null && !roomList.isEmpty()) {
			roomList.forEach(room -> room.changeStatus(RoomStatus.UNAVAILABLE));
			roomRepository.saveAll(roomList);
		}
	}

	// 숙소 상세보기용
	@Override
	@Transactional(readOnly = true)
	public LodgingDetailDTO getLodgingDetail(Long lodgingNo) {

		// 1. 숙소 기본 정보 조회
		Lodging lodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("해당 숙소를 찾을 수 없습니다. lodgingNo=" + lodgingNo));

		// 2. 숙소 이미지 목록 조회 (정렬 순서대로)
		List<LodgingImage> images = lodgingImageRepository.findByLodgingNoOrderBySortOrderAsc(lodgingNo);

		// 3. 객실 목록 조회
		List<Room> rooms = roomRepository.findByLodgingNo(lodgingNo);

		// 4. Lodging,Images,Rooms를 하나의 상세 DTO로 묶어서 반환
		return LodgingDetailDTO.builder().lodgingNo(lodging.getLodgingNo()) // 숙소 번호
				.lodgingNo(lodging.getLodgingNo()) // 숙소 번호
				.hostNo(lodging.getHostNo()) // 호스트 번호
				.lodgingName(lodging.getLodgingName()) // 숙소명
				.lodgingType(lodging.getLodgingType()) // 숙소 유형
				.region(lodging.getRegion()) // 지역
				.address(lodging.getAddress()) // 주소
				.detailAddress(lodging.getDetailAddress()) // 상세 주소
				.zipCode(lodging.getZipCode()) // 우편번호
				.latitude(lodging.getLatitude()) // 위도
				.longitude(lodging.getLongitude()) // 경도
				.description(lodging.getDescription()) // 설명
				.checkInTime(lodging.getCheckInTime()) // 체크인 시간
				.checkOutTime(lodging.getCheckOutTime()) // 체크아웃 시간
				.status(lodging.getStatus()) // 숙소 상태
				.images(images.stream().map(this::toLodgingImageDTO).toList()) // 이미지 목록
				.rooms(rooms.stream().map(this::toRoomSummaryDTO).toList()) // 객실 목록
				.build(); // 상세 DTO 생성
	}

	// DTO -> Entity 변환 메서드를 Impl 내부로 이동
	private Lodging toLodgingEntity(LodgingDTO lodgingDTO) {
		return Lodging.builder().lodgingNo(lodgingDTO.getLodgingNo()) // 숙소 번호 세팅
				.hostNo(lodgingDTO.getHostNo()) // 호스트 번호 세팅
				.lodgingName(lodgingDTO.getLodgingName()) // 숙소명 세팅
				.lodgingType(lodgingDTO.getLodgingType()) // 숙소 유형 세팅
				.region(lodgingDTO.getRegion()) // 지역 세팅
				.address(lodgingDTO.getAddress()) // 주소 세팅
				.detailAddress(lodgingDTO.getDetailAddress()) // 상세 주소 세팅
				.zipCode(lodgingDTO.getZipCode()) // 우편번호 세팅
				.latitude(lodgingDTO.getLatitude()) // 위도 세팅
				.longitude(lodgingDTO.getLongitude()) // 경도 세팅
				.description(lodgingDTO.getDescription()) // 설명 세팅
				.checkInTime(lodgingDTO.getCheckInTime()) // 체크인 시간 세팅
				.checkOutTime(lodgingDTO.getCheckOutTime()) // 체크아웃 시간 세팅
				.status(lodgingDTO.getStatus()) // 상태 세팅
				.build(); // Entity 생성
	}

	// Entity -> DTO 변환 메서드를 Impl 내부로 이동
	private LodgingDTO toLodgingDTO(Lodging lodging) {
		return LodgingDTO.builder().lodgingNo(lodging.getLodgingNo()) // 숙소 번호 세팅
				.hostNo(lodging.getHostNo()) // 호스트 번호 세팅
				.lodgingName(lodging.getLodgingName()) // 숙소명 세팅
				.lodgingType(lodging.getLodgingType()) // 숙소 유형 세팅
				.region(lodging.getRegion()) // 지역 세팅
				.address(lodging.getAddress()) // 주소 세팅
				.detailAddress(lodging.getDetailAddress()) // 상세 주소 세팅
				.zipCode(lodging.getZipCode()) // 우편번호 세팅
				.latitude(lodging.getLatitude()) // 위도 세팅
				.longitude(lodging.getLongitude()) // 경도 세팅
				.description(lodging.getDescription()) // 설명 세팅
				.checkInTime(lodging.getCheckInTime()) // 체크인 시간 세팅
				.checkOutTime(lodging.getCheckOutTime()) // 체크아웃 시간 세팅
				.status(lodging.getStatus()) // 상태 세팅
				.build(); // DTO 생성
	}

	// LodgingImage Entity -> DTO 변환도 Impl 내부에서 처리
	private LodgingImageDTO toLodgingImageDTO(LodgingImage lodgingImage) {
		return LodgingImageDTO.builder().imageNo(lodgingImage.getImageNo()) // 이미지 번호 세팅
				.imageUrl(lodgingImage.getImageUrl()) // 이미지 경로 세팅
				.sortOrder(lodgingImage.getSortOrder()) // 정렬 순서 세팅
				.build(); // DTO 생성
	}

	// RoomSummaryDTO -> Room Entity 변환도 Impl 내부에서 처리
	private Room toRoomEntity(RoomSummaryDTO roomDTO) {
		return Room.builder()
				.roomName(roomDTO.getRoomName()) // 객실명 세팅
				.roomType(roomDTO.getRoomType()) // 객실 유형 세팅
				.roomDescription(roomDTO.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(roomDTO.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(roomDTO.getPricePerNight()) // 1박 가격 세팅
				.roomCount(roomDTO.getRoomCount()) // 객실 수 세팅
				.status(roomDTO.getStatus() != null ? roomDTO.getStatus() : RoomStatus.AVAILABLE) // 상태 세팅
				.build(); // Entity 생성
	}

	// Room Entity -> RoomSummaryDTO 변환도 Impl 내부에서 처리
	private RoomSummaryDTO toRoomSummaryDTO(Room room) {
		return RoomSummaryDTO.builder().roomNo(room.getRoomNo()) // 객실 번호 세팅
				.roomName(room.getRoomName()) // 객실명 세팅
				.roomType(room.getRoomType()) // 객실 유형 세팅
				.roomDescription(room.getRoomDescription()) // 객실 설명 세팅
				.maxGuestCount(room.getMaxGuestCount()) // 최대 수용 인원 세팅
				.pricePerNight(room.getPricePerNight()) // 1박 가격 세팅
				.roomCount(room.getRoomCount()) // 객실 수 세팅
				.status(room.getStatus()) // 상태 세팅
				.build(); // DTO 생성
	}

}