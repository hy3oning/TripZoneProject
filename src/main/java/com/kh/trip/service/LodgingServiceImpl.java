package com.kh.trip.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
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
		if (lodging.getRegion() == null || lodging.getRegion().isBlank()) {
			throw new IllegalArgumentException("지역 정보는 필수입니다.");
		}

		// 주소 필수 체크
		if (lodging.getAddress() == null || lodging.getAddress().isBlank()) {
			throw new IllegalArgumentException("주소는 필수입니다.");
		}

		// 상태값이 비어있으면 기본값 ACTIVE로 저장
		// 엔티티에서 @Builder.Default를 설정했더라도
		// 혹시 외부에서 null로 넣어오는 경우를 방어하기 위해 한번 더 체크한다.
		Lodging saveLodging = lodging;

		if (lodging.getStatus() == null || lodging.getStatus().isBlank()) {
			saveLodging = Lodging.builder().lodgingNo(lodging.getLodgingNo()).hostNo(lodging.getHostNo())
					.lodgingName(lodging.getLodgingName()).lodgingType(lodging.getLodgingType())
					.region(lodging.getRegion()).address(lodging.getAddress()).detailAddress(lodging.getDetailAddress())
					.zipCode(lodging.getZipCode()).latitude(lodging.getLatitude()).longitude(lodging.getLongitude())
					.description(lodging.getDescription()).checkInTime(lodging.getCheckInTime())
					.checkOutTime(lodging.getCheckOutTime()).status("ACTIVE").build();
		}

		return lodgingRepository.save(saveLodging);
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
	public Lodging updateLodging(Long lodgingNo, Lodging lodging) {

	    // 기존 숙소 조회
	    Lodging findLodging = lodgingRepository.findById(lodgingNo)
	            .orElseThrow(() -> new NoSuchElementException("수정할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

	    
	    // 기존 엔티티의 값만 수정
	    // ===============================
	    findLodging.updateLodging(
	            lodging.getHostNo(),
	            lodging.getLodgingName(),
	            lodging.getLodgingType(),
	            lodging.getRegion(),
	            lodging.getAddress(),
	            lodging.getDetailAddress(),
	            lodging.getZipCode(),
	            lodging.getLatitude(),
	            lodging.getLongitude(),
	            lodging.getDescription(),
	            lodging.getCheckInTime(),
	            lodging.getCheckOutTime(),
	            lodging.getStatus()
	    );

	    // 수정된 엔티티 저장
	    return lodgingRepository.save(findLodging);
	}

	// 숙소 삭제
	@Override
	public void deleteLodging(Long lodgingNo) {

		Lodging findLodging = lodgingRepository.findById(lodgingNo)
				.orElseThrow(() -> new NoSuchElementException("삭제할 숙소가 존재하지 않습니다. lodgingNo=" + lodgingNo));

		lodgingRepository.delete(findLodging);
	}

}
