package com.kh.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGradeDTO {
	private Long gradeNo;
	private String gradeName;
	private Long minTotalAmount;
	private Long minStayCount;
	private double mileageRate;
	private String benefitDESC;
	private boolean status;
}
