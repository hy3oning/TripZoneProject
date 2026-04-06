package com.kh.trip.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.kh.trip.domain.Room;
import com.kh.trip.domain.enums.RoomStatus;

import jakarta.persistence.LockModeType;

// 객실 엔티티 전용 Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	// 특정 숙소 번호에 속한 객실 전체 조회
	List<Room> findByLodging_LodgingNo(Long lodgingNo);

	// 전체 객실을 roomNo 오름차순으로 조회
	List<Room> findAllByOrderByRoomNoAsc();

	// 상태별 객실 조회
	List<Room> findByStatusOrderByRoomNoAsc(RoomStatus status);

	// 객실명 키워드 검색
	List<Room> findByRoomNameContainingOrderByRoomNoAsc(String keyword);

	// 특정 숙소 번호 + 상태별 객실 조회
	List<Room> findByLodging_LodgingNoAndStatusOrderByRoomNoAsc(Long lodgingNo, RoomStatus status);

	// 특정 객실 조회 시 비관적 락 적용
	// 동시에 여러 트랜잭션이 같은 객실을 수정할 가능성을 대비한 구조
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Room> findByRoomNo(Long roomNo);
}