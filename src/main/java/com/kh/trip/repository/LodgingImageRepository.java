package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.LodgingImage;

// 숙소 이미지 전용 Repository
public interface LodgingImageRepository extends JpaRepository<LodgingImage, Long> {

	// 특정 숙소의 이미지들을 정렬 순서 오름차순으로 조회
	// 상세보기에서 이미지 순서대로 화면에 뿌릴 때 사용
	List<LodgingImage> findByLodging_LodgingNoOrderBySortOrderAsc(Long lodgingNo);

}
