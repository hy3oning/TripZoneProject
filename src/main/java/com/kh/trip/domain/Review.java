package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

// 리뷰 정보를 저장하는 엔티티
// DB의 REVIEWS 테이블과 매핑된다.
@Entity
@Table(name = "REVIEWS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseTimeEntity {

	// 리뷰 번호(PK)
	// Oracle 시퀀스 SEQ_REVIEWS를 사용해서 자동 생성된다.
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reviews")
	@SequenceGenerator(name = "seq_reviews", sequenceName = "SEQ_REVIEWS", allocationSize = 1)
	@Column(name = "REVIEW_NO")
	private Long reviewNo;

	// 어떤 예약에 대한 리뷰인지 나타내는 연관관계
	// 현재 구조에서는 예약 정보 검증과 중복 리뷰 방지에 사용된다.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOOKING_NO", nullable = false)
	private Booking booking;

	// 리뷰 작성자 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_NO", nullable = false)
	private User user;

	// 어떤 숙소에 대한 리뷰인지 나타내는 숙소 정보
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LODGING_NO", nullable = false)
	private Lodging lodging;

	// 평점
	// 1점부터 5점까지의 값을 가진다.
	@Column(name = "RATING", nullable = false)
	private Integer rating;

	// 리뷰 내용
	// 글자 수가 길어질 수 있으므로 @Lob 사용
	@Lob
	@Column(name = "CONTENT", nullable = false)
	private String content;

	// 평점 변경
	public void changeRating(Integer rating) {
		this.rating = rating;
	}

	// 리뷰 내용 변경
	public void changeContent(String content) {
		this.content = content;
	}
}