package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;

@Entity
@Table(name = "LODGING_IMAGES")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
@AllArgsConstructor
@Builder

public class LodgingImage extends BaseTimeEntity {

	@Id // 기본키
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lodging_images") // 시퀀스 방식 PK 생성
	@SequenceGenerator(name = "seq_lodging_images", sequenceName = "SEQ_LODGING_IMAGES", allocationSize = 1)
	@Column(name = "IMAGE_NO")
	private Long imageNo;

	@Column(name = "LODGING_NO", nullable = false) // 숙소 번호 FK
	private Long lodgingNo;

	@Column(name = "IMAGE_URL", nullable = false, length = 300) // 이미지 경로
	private String imageUrl;

	@Column(name = "SORT_ORDER", nullable = false) // 정렬 순서
	private Integer sortOrder;

}
