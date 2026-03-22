package com.kh.trip.domain;

import org.hibernate.annotations.Check;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.InquiryMessageSenderType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "INQUIRY_MESSAGES")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Check(constraints = "MESSAGE_CONTENT IS NOT NULL OR IMAGE_URL IS NOT NULL")
public class InquiryMessage extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INQUIRY_MESSAGES")
	@SequenceGenerator(name = "SEQ_INQUIRY_MESSAGES", sequenceName = "SEQ_INQUIRY_MESSAGES", allocationSize = 1)
	@Column(name = "MESSAGE_NO")
	private Long messageNo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INQUIRY_ROOM_NO", nullable = false)
	private InquiryRoom inquiryRoom;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SENDER_USER_NO", nullable = false)
	private User senderUser;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SENDER_TYPE", nullable = false, length = 20)
	private InquiryMessageSenderType senderType;
	
	@Column(name = "MESSAGE_CONTENT", length = 2000)
	private String messageContent;
	
	@Column(name = "IMAGE_URL", length = 300)
	private String imageUrl;
	
	@Builder.Default
	@Column(name = "READ_CHECK", nullable = false)
	private boolean readCheck = false; //읽으면 TRUE, 안읽으면 false
}
