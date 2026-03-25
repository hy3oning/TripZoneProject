package com.kh.trip.domain;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "ROOM_IMAGES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RoomImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_room_images")
	@SequenceGenerator(
			name = "seq_room_images",          // JPA 내부에서 사용할 시퀀스 이름
			sequenceName = "SEQ_ROOM_IMAGES",  // 실제 Oracle 시퀀스 이름
			allocationSize = 1)
	@Column(name = "ROOM_IMAGE_NO")
	private Long roomImageNo;

	@Column(name = "ROOM_NO", nullable = false)
	private Long roomNo; // 어떤 객실에 속한 이미지인지 저장하는 객실 번호

	@Column(name = "IMAGE_URL", nullable = false, length = 300)
	private String imageUrl; // DB에서 VARCHAR2(300) 이므로 길이를 300으로 맞춤

	@Column(name = "SORT_ORDER", nullable = false)
	private Integer sortOrder; // 이미지 정렬 순서

	@Column(name = "REG_DATE", insertable = false, updatable = false)
	private LocalDateTime regDate; // DB DEFAULT SYSDATE 값을 그대로 사용하기 위한 등록일

}
