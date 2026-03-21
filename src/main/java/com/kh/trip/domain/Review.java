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
@Table(name = "REVIEWS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {

	@Id 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reviews") 
	@SequenceGenerator(name = "seq_reviews", sequenceName = "SEQ_REVIEWS", allocationSize = 1)
	@Column(name = "REVIEW_NO") // REVIEW_NO 컬럼과 매핑
	private Long reviewNo; // 리뷰 번호

	@Column(name = "BOOKING_NO", nullable = false) // 예약 번호 (필수)
	private Long bookingNo; // 어떤 예약에 대한 리뷰인지

	@Column(name = "USER_NO", nullable = false) // 작성 회원 번호 (필수)
	private Long userNo; // 리뷰 작성자 회원 번호

	@Column(name = "LODGING_NO", nullable = false) // 숙소 번호 (필수)
	private Long lodgingNo; // 어떤 숙소에 대한 리뷰인지

	@Column(name = "RATING", nullable = false) // 평점 컬럼
	private Integer rating; // 평점 (1~5)

	@Column(name = "CONTENT", nullable = false, length = 1000) // 리뷰 내용
	private String content; // 리뷰 본문

}
