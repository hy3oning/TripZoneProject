package com.kh.trip.service;

import java.util.List;
import java.util.NoSuchElementException;

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
	public Lodging createLodging(Lodging lodging, List<Room> roomList) {
		// null 체크
		if (lodging == null) {
			throw new IllegalArgumentException("숙소 정보가 없습니다.");
		}

		// 숙소명 필수 체크
		if (lodging.getLodgingName() == null || lodging.getLodgingName().isBlank()) {
			throw new IllegalArgumentException("숙소명은 필수입니다.");
		}

		// 지역 필수 체크
		if (lodging.getLodgingType() == null) {
			throw new IllegalArgumentException("숙소 유형은 필수입니다.");
		}

		// 주소 필수 체크
		if (lodging.getAddress() == null || lodging.getAddress().isBlank()) {
			throw new IllegalArgumentException("주소는 필수입니다.");
		}

		if (lodging.getStatus() == null) {
			lodging.changeStatus(LodgingStatus.ACTIVE);
		}

		Lodging savedLodging = lodgingRepository.save(lodging);

		if (roomList != null && !roomList.isEmpty()) {
			roomList.forEach(room -> room.changeLodgingNo(savedLodging.getLodgingNo()));
			roomRepository.saveAll(roomList);
		}
		return savedLodging;
	}

	// 숙소 단건 조회
	@Override
	@Transactional(readOnly = true)
	public Lodging getLodging(Long lodgingNo) {
		return lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("해당 숙소를 찾을 수 없습니다. lodgingNo=" + lodgingNo));
	}

	// 숙소 전체 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<Lodging> getAllLodgings() {
		return lodgingRepository.findAll();
	}

	// 지역으로 숙소 목록 조회
	@Override
	@Transactional(readOnly = true)
	public List<Lodging> getLodgingsByRegion(String region) {
		if (region == null || region.isBlank()) {
			throw new IllegalArgumentException("지역 값이 비어 있습니다.");
		}

		return lodgingRepository.findByRegionAndStatus(region, LodgingStatus.ACTIVE);
	}

	// 숙소명 키워드 검색
	@Override
	@Transactional(readOnly = true)
	public List<Lodging> searchLodgingsByName(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			throw new IllegalArgumentException("검색어가 비어 있습니다.");
		}

		return lodgingRepository.findByLodgingNameContainingAndStatus(keyword, LodgingStatus.ACTIVE);
	}

	// 숙소 수정
	@Override
	public Lodging updateLodging(Long lodgingNo, LodgingDTO lodgingDTO) {

		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("수정할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		applyLodgingUpdate(findLodging, lodgingDTO);

		return lodgingRepository.save(findLodging);
	}
	
	// 숙소 수정값 반영 메서드 domain에 있던 수정 기능을 Service로 옮긴 버전
	private void applyLodgingUpdate(Lodging findLodging, LodgingDTO lodgingDTO) {

		if (lodgingDTO.getLodgingName() != null && !lodgingDTO.getLodgingName().isBlank()) {
			findLodging.changeLodgingName(lodgingDTO.getLodgingName()); // 수정 setLodgingName -> changeLodgingName
		}

		if (lodgingDTO.getDescription() != null) {
			findLodging.changeDescription(lodgingDTO.getDescription()); // 수정 setDescription -> changeDescription
		}

		if (lodgingDTO.getCheckInTime() != null) {
			findLodging.changeCheckInTime(lodgingDTO.getCheckInTime()); // 수정 setCheckInTime -> changeCheckInTime
		}

		if (lodgingDTO.getCheckOutTime() != null) {
			findLodging.changeCheckOutTime(lodgingDTO.getCheckOutTime()); // 수정 setCheckOutTime -> changeCheckOutTime
		}

		if (lodgingDTO.getStatus() != null) {
			findLodging.changeStatus(lodgingDTO.getStatus()); // 수정 setStatus -> changeStatus
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

				// 이미지 엔티티 리스트를 DTO 리스트로 변환
				.images(images.stream().map(LodgingImageDTO::fromEntity).toList())

				// 객실 엔티티 리스트를 DTO 리스트로 변환
				.rooms(rooms.stream().map(RoomSummaryDTO::fromEntity).toList())

				.build(); // 최종 상세 DTO 생성
	}

}
