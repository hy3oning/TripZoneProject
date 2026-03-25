package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.InquiryStatus;
import com.kh.trip.domain.enums.InquiryType;

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
import lombok.ToString;

@Entity
@Table(name = "INQUIRYS")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inquiry extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INQUIRY")
	@SequenceGenerator(name = "SEQ_INQUIRY", sequenceName = "SEQ_INQUIRY",allocationSize = 1)
	@Column(name = "INQUIRY_NO")
	private Long inquiryNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_NO", nullable = false)
	private User user;
	
	@Column(name = "INQUIRY_TYPE", nullable = false)
	private InquiryType inquiryType;
	
	@Column(name = "TITLE", nullable = false, length = 100)
	private String title;
	
	@Column(name = "CONTENT", nullable = false, length = 500)
	private String content;
	
	@Builder.Default
	@Column(name = "STATUS", nullable = false)
	private InquiryStatus status = InquiryStatus.PENDING;
	
	public void changeTitle(String title) {
		this.title = title;
	}
	
	public void changeContent(String content) {
		this.content = content;
	}
	
}
