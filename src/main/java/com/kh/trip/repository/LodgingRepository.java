package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.enums.LodgingStatus;

public interface LodgingRepository extends JpaRepository<Lodging, Long> {
	/**
	 * 지역으로 숙소 목록 조회
	 * 
	 * 예: region = "서울" → REGION 컬럼이 "서울"인 숙소 목록 반환
	 */
	List<Lodging> findByRegionAndStatus(String region, LodgingStatus status);

	/**
	 * 숙소명에 특정 키워드가 포함된 숙소 검색
	 * 
	 * Containing = SQL의 LIKE %키워드% 개념
	 * 
	 * 예: keyword = "호텔" → 숙소명에 "호텔"이 들어간 숙소 검색
	 */
	List<Lodging> findByLodgingNameContainingAndStatus(String keyword, LodgingStatus status);

	/**
	 * 상태값으로 숙소 조회
	 * 
	 * 예: status = "ACTIVE" → 활성화된 숙소만 조회
	 */
	List<Lodging> findByStatus(LodgingStatus status);

}
