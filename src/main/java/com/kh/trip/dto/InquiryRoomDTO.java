package com.kh.trip.dto;

import com.kh.trip.domain.enums.InquiryRoomStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryRoomDTO {
	private Long inquiryRoomNo;
	private Long userNo;
	private Long hostNo;
	private Long lodgingNo;
	private InquiryRoomStatus status;
}
