package com.kh.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private Long userNo;
	private String userName;
	private String email;
	private String phone;
	private Long gradeNo;
	private Long mileage;
	private String enabled;

}