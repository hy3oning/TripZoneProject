package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.RoomStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 객실 정보를 저장하는 엔티티
// DB의 ROOMS 테이블과 매핑된다.
@Entity
@Table(name = "ROOMS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room extends BaseTimeEntity {

	// 객실 번호(PK)
	// Oracle 시퀀스 SEQ_ROOMS를 사용해서 자동 생성된다.
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rooms")
	@SequenceGenerator(
			name = "seq_rooms",          // JPA 내부에서 사용할 시퀀스 이름
			sequenceName = "SEQ_ROOMS",  // 실제 Oracle DB 시퀀스 이름
			allocationSize = 1)
	@Column(name = "ROOM_NO")
	private Long roomNo;

	// 객실이 속한 숙소 정보
	// 하나의 숙소에 여러 객실이 연결될 수 있으므로 ManyToOne 관계이다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LODGING_NO", nullable = false)
	private Lodging lodging;

	// 객실 이름
	// 예: 디럭스 더블룸, 오션뷰 스위트
	@Column(name = "ROOM_NAME", nullable = false, length = 200)
	private String roomName;

	// 객실 유형
	// 현재 구조에서는 String으로 저장하고 있다.
	// 예: SINGLE, DOUBLE, SUITE
	@Column(name = "ROOM_TYPE", nullable = false, length = 50)
	private String roomType;

	// 객실 설명
	// 설명이 길어질 수 있어서 @Lob 사용
	@Lob
	@Column(name = "ROOM_DESCRIPTION")
	private String roomDescription;

	// 최대 수용 인원
	@Column(name = "MAX_GUEST_COUNT", nullable = false)
	private Integer maxGuestCount;

	// 1박당 가격
	@Column(name = "PRICE_PER_NIGHT", nullable = false)
	private Integer pricePerNight;

	// 동일 타입 객실 수
	@Column(name = "ROOM_COUNT", nullable = false)
	private Integer roomCount;

	// 객실 상태
	// AVAILABLE : 예약 가능
	// UNAVAILABLE : 예약 불가
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "STATUS", nullable = false, length = 20)
	private RoomStatus status = RoomStatus.AVAILABLE;

	// 객실이 속한 숙소 변경
	public void changeLodging(Lodging lodging) {
		this.lodging = lodging;
	}

	// 객실명 변경
	public void changeRoomName(String roomName) {
		this.roomName = roomName;
	}

	// 객실 설명 변경
	public void changeRoomDescription(String roomDescription) {
		this.roomDescription = roomDescription;
	}

	// 1박 가격 변경
	public void changePricePerNight(Integer pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	// 객실 수 변경
	public void changeRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
	}

	// 객실 상태 변경
	public void changeStatus(RoomStatus status) {
		this.status = status;
	}

	// 객실 유형 변경
	public void changeRoomType(String roomType) {
		this.roomType = roomType;
	}

	// 최대 수용 인원 변경
	public void changeMaxGuestCount(Integer maxGuestCount) {
		this.maxGuestCount = maxGuestCount;
	}
}