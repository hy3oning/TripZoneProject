package com.kh.trip.domain;

import java.util.ArrayList;
import java.util.List;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// DB의 LODGINGS 테이블과 매핑되는 숙소 엔티티 클래스
// 숙소의 기본 정보, 상태, 이미지 목록 등을 관리한다.
@Entity
@Table(name = "LODGINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lodging extends BaseTimeEntity {

	// 숙소 번호(PK)
	// Oracle 시퀀스 SEQ_LODGINGS를 이용해서 자동 생성된다.
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lodgings")
	@SequenceGenerator(name = "seq_lodgings", sequenceName = "SEQ_LODGINGS", allocationSize = 1)
	@Column(name = "LODGING_NO")
	private Long lodgingNo;

	// 이 숙소를 등록한 판매자(호스트) 정보
	// 여러 숙소가 하나의 호스트에 속할 수 있으므로 ManyToOne 관계이다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HOST_NO", nullable = false)
	private HostProfile host;

	// 숙소 이름
	// 예: 제주 오션 호텔, 강남 스테이
	@Column(name = "LODGING_NAME", nullable = false, length = 200)
	private String lodgingName;

	// 숙소 유형
	// 예: HOTEL, PENSION, GUESTHOUSE, MOTEL
	@Enumerated(EnumType.STRING)
	@Column(name = "LODGING_TYPE", nullable = false, length = 50)
	private LodgingType lodgingType;

	// 숙소가 위치한 지역
	// 예: 서울, 부산, 제주
	@Column(name = "REGION", nullable = false, length = 100)
	private String region;

	// 숙소 기본 주소
	@Column(name = "ADDRESS", nullable = false, length = 300)
	private String address;

	// 숙소 상세 주소
	// 예: 101호, 3층
	@Column(name = "DETAIL_ADDRESS", length = 300)
	private String detailAddress;

	// 우편번호
	@Column(name = "ZIP_CODE", length = 20)
	private String zipCode;

	// 지도 표시용 위도
	@Column(name = "LATITUDE")
	private Double latitude;

	// 지도 표시용 경도
	@Column(name = "LONGITUDE")
	private Double longitude;

	// 숙소 설명
	// 글자 수가 길어질 수 있으므로 @Lob 사용
	@Lob
	@Column(name = "DESCRIPTION")
	private String description;

	// 체크인 시간
	// 예: 15:00
	@Column(name = "CHECK_IN_TIME", length = 20)
	private String checkInTime;

	// 체크아웃 시간
	// 예: 11:00
	@Column(name = "CHECK_OUT_TIME", length = 20)
	private String checkOutTime;

	// 숙소 상태값
	// ACTIVE : 운영 중
	// INACTIVE : 비활성화
	// Builder로 생성할 때 값이 없으면 기본값은 ACTIVE
	@Enumerated(EnumType.STRING)
	@Builder.Default
	@Column(name = "STATUS", nullable = false, length = 20)
	private LodgingStatus status = LodgingStatus.ACTIVE;

	// 숙소 이미지 목록
	// 숙소 하나에 여러 장의 이미지가 연결될 수 있으므로 OneToMany 관계이다.
	// cascade = ALL : 숙소 저장/수정/삭제 시 이미지도 함께 반영
	// orphanRemoval = true : 연관관계에서 빠진 이미지는 자동 삭제
	@OneToMany(mappedBy = "lodging", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<LodgingImage> imageList = new ArrayList<>();

	// 숙소 상태 변경
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

	// 숙소 이미지 엔티티를 직접 추가하는 메서드
	// 이미지 순서를 자동으로 지정하고, 양방향 연관관계도 함께 설정한다.
	public void addImage(LodgingImage image) {
		// 현재 이미지 개수 기준으로 정렬 순서를 1부터 부여
		image.changeOrd(this.imageList.size() + 1);

		// 이미지 쪽에도 현재 숙소 정보를 세팅해서 양방향 관계를 맞춘다.
		image.changeLodging(this);

		// 최종적으로 이미지 목록에 추가
		imageList.add(image);
	}

	// 파일명만 받아서 LodgingImage 객체를 생성한 뒤 추가하는 편의 메서드
	public void addImageString(String fileName) {
		LodgingImage lodgingImage = LodgingImage.builder()
				.fileName(fileName)
				.build();

		addImage(lodgingImage);
	}

	// 기존 숙소 이미지 목록 전체 비우기
	// 수정 시 기존 이미지 삭제 후 새 이미지로 교체할 때 사용한다.
	public void clearList() {
		this.imageList.clear();
	}
}