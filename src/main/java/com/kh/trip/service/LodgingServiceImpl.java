package com.kh.trip.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.repository.LodgingRepository;

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

	// 숙소 등록
	@Override
	public Lodging createLodging(Lodging lodging) {
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
			lodging.setStatus(LodgingStatus.ACTIVE);
		}

		return lodgingRepository.save(lodging);
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

		return lodgingRepository.findByRegion(region);
	}

	// 숙소명 키워드 검색
	@Override
	@Transactional(readOnly = true)
	public List<Lodging> searchLodgingsByName(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			throw new IllegalArgumentException("검색어가 비어 있습니다.");
		}

		return lodgingRepository.findByLodgingNameContaining(keyword);
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

		if (lodgingDTO.getHostNo() != null) {
			findLodging.setHostNo(lodgingDTO.getHostNo());
		}

		if (lodgingDTO.getLodgingName() != null && !lodgingDTO.getLodgingName().isBlank()) {
			findLodging.setLodgingName(lodgingDTO.getLodgingName());
		}

		if (lodgingDTO.getLodgingType() != null) {
			findLodging.setLodgingType(lodgingDTO.getLodgingType());
		}

		if (lodgingDTO.getRegion() != null && !lodgingDTO.getRegion().isBlank()) {
			findLodging.setRegion(lodgingDTO.getRegion());
		}

		if (lodgingDTO.getAddress() != null && !lodgingDTO.getAddress().isBlank()) {
			findLodging.setAddress(lodgingDTO.getAddress());
		}

		if (lodgingDTO.getDetailAddress() != null) {
			findLodging.setDetailAddress(lodgingDTO.getDetailAddress());
		}

		if (lodgingDTO.getZipCode() != null) {
			findLodging.setZipCode(lodgingDTO.getZipCode());
		}

		if (lodgingDTO.getLatitude() != null) {
			findLodging.setLatitude(lodgingDTO.getLatitude());
		}

		if (lodgingDTO.getLongitude() != null) {
			findLodging.setLongitude(lodgingDTO.getLongitude());
		}

		if (lodgingDTO.getDescription() != null) {
			findLodging.setDescription(lodgingDTO.getDescription());
		}

		if (lodgingDTO.getCheckInTime() != null) {
			findLodging.setCheckInTime(lodgingDTO.getCheckInTime());
		}

		if (lodgingDTO.getCheckOutTime() != null) {
			findLodging.setCheckOutTime(lodgingDTO.getCheckOutTime());
		}

		if (lodgingDTO.getStatus() != null) {
			findLodging.setStatus(lodgingDTO.getStatus());
		}
	}

	// 숙소 삭제
	@Override
	public void deleteLodging(Long lodgingNo) {

		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("삭제할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		lodgingRepository.delete(findLodging);
	}

}
