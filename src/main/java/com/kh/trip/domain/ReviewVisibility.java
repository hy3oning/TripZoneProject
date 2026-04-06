package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 리뷰 공개/숨김 상태를 관리하는 엔티티
// DB의 REVIEW_VISIBILITY 테이블과 매핑된다.
@Entity
@Table(name = "REVIEW_VISIBILITY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewVisibility extends BaseTimeEntity {

	// 리뷰 번호를 그대로 PK로 사용
	// 리뷰 1개당 공개 여부 정보 1개를 가진다.
	@Id
	@Column(name = "REVIEW_NO")
	private Long reviewNo;

	// 리뷰 공개 여부
	// true : 사용자에게 보이는 리뷰
	// false : 관리자에 의해 숨김 처리된 리뷰
	@Builder.Default
	@Column(name = "VISIBLE", nullable = false)
	private boolean visible = true;
	
	// 공개 여부 변경
	public void changeVisible(boolean visible) {
		this.visible = visible;
	}
}
