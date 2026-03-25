package com.kh.trip.repository;

import java.util.List;

import com.kh.trip.domain.RoomImage;

public interface RoomImageRepository {

	// 특정 객실 번호의 이미지들을 정렬 순서대로 조회
	List<RoomImage> findByRoomNoOrderBySortOrderAsc(Long roomNo);

	// 특정 객실 번호의 이미지 전체 삭제
	void deleteByRoomNo(Long roomNo);

}
