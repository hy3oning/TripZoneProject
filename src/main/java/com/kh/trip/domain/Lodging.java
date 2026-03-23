package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//DB 테이블과 매핑되는 클래스
@Entity

//LODGINGS 테이블과 매핑된다.
@Table(name = "LODGINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Lodging extends BaseTimeEntity {

	// 숙소 번호 (Primary Key)
	// DB에서 숙소를 구분하는 고유한 ID 값
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lodgings")
	@SequenceGenerator(name = "seq_lodgings", sequenceName = "SEQ_LODGINGS", allocationSize = 1)
	@Column(name = "LODGING_NO")
	private Long lodgingNo;

	// 숙소를 등록한 호스트(판매자)의 번호
	// USERS 테이블의 USER_NO와 연결될 수 있는 값
	@Column(name = "HOST_NO", nullable = false)
	private Long hostNo;

	// 숙소 이름
	// 최대 200자까지 저장 가능
	// 반드시 값이 있어야 한다
	@Column(name = "LODGING_NAME", nullable = false, length = 200)
	private String lodgingName;

	// 숙소 유형
	// 예: HOTEL, PENSION, GUESTHOUSE, MOTEL 등
	@Enumerated(EnumType.STRING)
	@Column(name = "LODGING_TYPE", nullable = false, length = 50)
	private LodgingType lodgingType;

	// 숙소 지역
	// 예: 서울, 부산, 제주
	@Column(name = "REGION", nullable = false, length = 100)
	private String region;

	// 숙소 기본 주소
	// 예: 서울시 강남구 테헤란로
	@Column(name = "ADDRESS", nullable = false, length = 300)
	private String address;

	// 숙소 상세 주소
	// 예: 101호, 3층
	@Column(name = "DETAIL_ADDRESS", length = 300)
	private String detailAddress;

	// 숙소 우편번호
	@Column(name = "ZIP_CODE", length = 20)
	private String zipCode;

	// 지도 표시용 위도 좌표
	@Column(name = "LATITUDE")
	private Double latitude;

	// 지도 표시용 경도 좌표
	@Column(name = "LONGITUDE")
	private Double longitude;

	// 숙소 설명
	// 숙소 소개나 특징 등을 작성하는 필드
	@Column(name = "DESCRIPTION", length = 2000)
	private String description;

	// 체크인 시간
	// 예: 15:00
	@Column(name = "CHECK_IN_TIME", length = 20)
	private String checkInTime;

	// 체크아웃 시간
	// 예: 11:00
	@Column(name = "CHECK_OUT_TIME", length = 20)
	private String checkOutTime;

	// 숙소 상태
	// ACTIVE : 운영중
	// INACTIVE : 비활성화
	// DELETED : 삭제
	// Builder 사용 시 기본값 ACTIVE
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "STATUS", nullable = false, length = 20)
	private LodgingStatus status = LodgingStatus.ACTIVE;

	// 상태 변경
	public void changeStatus(LodgingStatus status) {
		this.status = status;
	}

	// 숙소명 변경
	public void changeLodgingName(String lodgingName) {
		this.lodgingName = lodgingName;
	}

	// 숙소 설명 변경
	public void changeDescription(String description) {
		this.description = description;
	}

	// 체크인 시간 변경
	public void changeCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	// 체크아웃 시간 변경
	public void changeCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

}