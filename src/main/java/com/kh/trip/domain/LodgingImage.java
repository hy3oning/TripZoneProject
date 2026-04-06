package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 숙소 이미지 테이블(LODGING_IMAGES)과 매핑되는 엔티티
// 숙소 한 곳에 여러 장의 이미지가 연결된다.
@Entity
@Table(name = "LODGING_IMAGES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LodgingImage extends BaseTimeEntity {

	// 숙소 이미지 번호(PK)
	@Id
	@Column(name = "LODGING_IMAGE_NO")
	private Long imageNo;

	// 레거시 DB와의 호환을 위해 함께 관리하는 이미지 번호 컬럼
	@Column(name = "IMAGE_NO", nullable = false)
	private Long legacyImageNo;

	// 숙소 번호(FK 값)
	// insertable/updatable = false 이므로 읽기 전용으로만 사용된다.
	// 실제 연관관계 저장은 아래 lodging 필드가 담당한다.
	@Column(name = "LODGING_NO", nullable = false, insertable = false, updatable = false)
	private Long lodgingNo;

	// 어떤 숙소의 이미지인지 나타내는 연관관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LODGING_NO", nullable = false)
	private Lodging lodging;

	// 업로드된 이미지 파일명
	@Column(name = "FILE_NAME", nullable = false, length = 300)
	private String fileName;

	// 이미지 정렬 순서
	// 숙소 상세 화면에서 첫 번째 이미지, 두 번째 이미지 순서를 관리할 때 사용
	@Column(name = "SORT_ORDER", nullable = false)
	private Integer sortOrder;

	// 이미지 정렬 순서 변경
	public void changeOrd(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	// 이미지가 속한 숙소 변경
	public void changeLodging(Lodging lodging) {
		this.lodging = lodging;
	}

	// 이미지 번호를 세팅하는 메서드
	// 현재 구조에서는 imageNo와 legacyImageNo를 같은 값으로 맞춰서 저장한다.
	public void assignImageNo(Long imageNo) {
		this.imageNo = imageNo;
		this.legacyImageNo = imageNo;
	}
}