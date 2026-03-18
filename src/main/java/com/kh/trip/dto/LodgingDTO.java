package com.kh.trip.dto;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LodgingDTO
 * 
 * DTO(Data Transfer Object)는 계층 간 데이터를 주고받기 위한 객체
 * 
 * 
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class LodgingDTO {

	private Long lodgingNo;
	private Long hostNo;
	private String lodgingName;
	private LodgingType lodgingType;
	private String region;
	private String address;
	private String detailAddress;
	private String zipCode;
	private Double latitude;
	private Double longitude;
	private String description;
	private String checkInTime;
	private String checkOutTime;
	private LodgingStatus status;

	/**
	 * Entity → DTO 변환 메서드
	 * 
	 * Controller에서 응답을 보낼 때 Lodging 엔티티를 LodgingDTO로 바꾸는 데 사용한다.
	 */
	public static LodgingDTO fromEntity(Lodging lodging) {
		return LodgingDTO.builder().lodgingNo(lodging.getLodgingNo()).hostNo(lodging.getHostNo())
				.lodgingName(lodging.getLodgingName()).lodgingType(lodging.getLodgingType()).region(lodging.getRegion())
				.address(lodging.getAddress()).detailAddress(lodging.getDetailAddress()).zipCode(lodging.getZipCode())
				.latitude(lodging.getLatitude()).longitude(lodging.getLongitude()).description(lodging.getDescription())
				.checkInTime(lodging.getCheckInTime()).checkOutTime(lodging.getCheckOutTime())
				.status(lodging.getStatus()).build();
	}

	/**
	 * DTO → Entity 변환 메서드
	 * 
	 * 클라이언트에서 전달받은 DTO를 Service 계층으로 넘기기 전에 Entity로 바꿀 때 사용한다.
	 */
	public Lodging toEntity() {
		return Lodging.builder().lodgingNo(lodgingNo).hostNo(hostNo).lodgingName(lodgingName).lodgingType(lodgingType)
				.region(region).address(address).detailAddress(detailAddress).zipCode(zipCode).latitude(latitude)
				.longitude(longitude).description(description).checkInTime(checkInTime).checkOutTime(checkOutTime)
				.status(status).build();
	}

}
