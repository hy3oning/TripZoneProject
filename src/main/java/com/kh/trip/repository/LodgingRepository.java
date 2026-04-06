package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.enums.LodgingStatus;

// 숙소 엔티티 전용 Repository
// DB에서 숙소 데이터를 조회/저장/수정/삭제할 때 사용한다.
public interface LodgingRepository extends JpaRepository<Lodging, Long> {

	// 지역 + 상태값으로 숙소 목록 조회
	// 예: 서울 지역의 ACTIVE 숙소만 조회
	List<Lodging> findByRegionAndStatus(String region, LodgingStatus status);

	// 숙소명에 특정 키워드가 포함된 숙소 검색
	// 예: "호텔" 검색 시 숙소명에 호텔이 들어간 숙소 조회
	List<Lodging> findByLodgingNameContainingAndStatus(String keyword, LodgingStatus status);

	// 상태값으로 숙소 조회
	// 예: ACTIVE 숙소 전체 조회
	List<Lodging> findByStatus(LodgingStatus status);

	// 특정 호스트가 등록한 숙소 목록 조회
	// host 객체 안의 hostNo 기준으로 조회한다.
	List<Lodging> findByHost_HostNo(Long hostNo);

	// ACTIVE 상태 숙소를 페이징 조회
	Page<Lodging> findByStatus(LodgingStatus status, Pageable pageable);

	// 숙소 상세 조회용 메서드
	// imageList까지 함께 조회해서 상세보기 시 추가 쿼리를 줄이기 위해 사용
	@EntityGraph(attributePaths = "imageList")
	@Query("select l from Lodging l where l.lodgingNo = :lodgingNo")
	Optional<Lodging> selectOne(@Param("lodgingNo") Long lodgingNo);

	// 숙소 목록 조회용 메서드
	// 대표 이미지 1장(sortOrder = 1)만 함께 조회한다.
	// 이미지가 없는 숙소도 포함하기 위해 LEFT JOIN 사용
	@Query("select l, li from Lodging l left join l.imageList li on li.sortOrder = 1 where l.status = LodgingStatus.ACTIVE")
	List<Object[]> selectList();
}