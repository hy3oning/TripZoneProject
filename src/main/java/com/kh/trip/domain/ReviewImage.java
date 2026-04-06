package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 리뷰 이미지 정보를 저장하는 엔티티
// DB의 REVIEW_IMAGES 테이블과 매핑된다.
@Entity
@Table(name = "REVIEW_IMAGES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewImage extends BaseTimeEntity {

	// 리뷰 이미지 번호(PK)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_review_images")
	@SequenceGenerator(
			name = "seq_review_images",         // JPA 내부 시퀀스 이름
			sequenceName = "SEQ_REVIEW_IMAGES", // 실제 Oracle 시퀀스 이름
			allocationSize = 1)
	@Column(name = "REVIEW_IMAGE_NO")
	private Long reviewImageNo;

	// 어떤 리뷰의 이미지인지 나타내는 연관관계
	// 여러 이미지가 하나의 리뷰에 속할 수 있으므로 ManyToOne 관계이다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REVIEW_NO", nullable = false)
	private Review review;

	// 리뷰 이미지 URL 또는 저장 경로
	@Column(name = "IMAGE_URL", nullable = false, length = 300)
	private String imageUrl;

	// 이미지 정렬 순서
	// 상세 화면에서 이미지 표시 순서를 유지하기 위해 사용한다.
	@Column(name = "SORT_ORDER", nullable = false)
	private Integer sortOrder;
}