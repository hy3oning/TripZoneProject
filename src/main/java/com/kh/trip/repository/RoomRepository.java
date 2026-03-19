package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Room;

//Room Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	// 특정 숙소 번호에 해당하는 객실 목록 조회
	List<Room> findByLodgingNo(Long lodgingNo);

}
