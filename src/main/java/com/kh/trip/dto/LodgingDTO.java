package com.kh.trip.dto;

import java.util.List;

import com.kh.trip.domain.enums.LodgingStatus;
import com.kh.trip.domain.enums.LodgingType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private List<RoomSummaryDTO> roomDTO;

}
