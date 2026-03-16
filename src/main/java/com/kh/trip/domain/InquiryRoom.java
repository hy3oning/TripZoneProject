package com.kh.trip.domain;

import com.kh.trip.domain.enums.InquiryRoomStatus;
import com.kh.trip.domain.enums.InquiryRoomTargetType;
import com.kh.trip.domain.enums.InquiryRoomInquiryType;

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
@Table(name = "INQUIRY_ROOMS")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InquiryRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INQUIRY_ROOMS")
	@SequenceGenerator(name = "SEQ_INQUIRY_ROOMS", sequenceName = "SEQ_INQUIRY_ROOMS")
	@Column(name = "INQUIRY_ROOM_NO")
	private Long inquiryRoomNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_NO", nullable = false)
	private User user;
	
	@Column(name = "TARGET_TYPE", nullable = false, length = 20)
	private InquiryRoomTargetType targetType;
	
	@Column(name = "INQUIRY_TYPE", nullable = false, length = 30)
	private InquiryRoomInquiryType inquiryType;
	
	@Column(name = "TITLE", nullable = false, length = 200)
	private String title;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ADMIN_USER_NO", nullable = false)
	private User adminUser;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "LODGING_NO", nullable = false)
//	private Lodging lodging;
	
	@Builder.Default
	@Column(name = "STATUS ", nullable = false, length = 20)
	private InquiryRoomStatus status = InquiryRoomStatus.OPEN;
}
