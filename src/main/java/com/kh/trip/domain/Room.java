package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;
import com.kh.trip.domain.enums.RoomStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity // JPA 엔티티 선언
@Table(name = "LODGING_IMAGES") // LODGING_IMAGES 테이블과 매핑
@Getter // getter 자동 생성
@Setter // setter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected
@AllArgsConstructor // 전체 필드 생성자
@Builder // builder 패턴 사용
public class Room extends BaseTimeEntity{
	
	@Id // 기본키
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rooms") // 시퀀스 방식 PK 생성
    @SequenceGenerator(
        name = "seq_rooms",      // JPA 내부 시퀀스 이름
        sequenceName = "SEQ_ROOMS", // 실제 DB 시퀀스 이름
        allocationSize = 1       
    )
    @Column(name = "ROOM_NO") // ROOM_NO 컬럼
    private Long roomNo;

    @Column(name = "LODGING_NO", nullable = false) // 어떤 숙소에 속한 객실인지
    private Long lodgingNo;

    @Column(name = "ROOM_NAME", nullable = false, length = 200) // 객실명
    private String roomName;

    @Column(name = "ROOM_TYPE", nullable = false, length = 50) // 객실 유형
    private String roomType;

    @Column(name = "ROOM_DESCRIPTION", length = 2000) // 객실 설명
    private String roomDescription;

    @Column(name = "MAX_GUEST_COUNT", nullable = false) // 최대 인원
    private Integer maxGuestCount;

    @Column(name = "PRICE_PER_NIGHT", nullable = false) // 1박 가격
    private Integer pricePerNight;

    @Column(name = "ROOM_COUNT", nullable = false) // 동일 객실 수
    private Integer roomCount;

    @Enumerated(EnumType.STRING) 
    @Builder.Default 
    @Column(name = "STATUS", nullable = false, length = 20) // 객실 상태
    private RoomStatus status = RoomStatus.AVAILABLE;
	

}
