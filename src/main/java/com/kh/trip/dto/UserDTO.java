package com.kh.trip.dto;

import com.kh.trip.domain.User;

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

	public static UserDTO fromEntity(User user) {
		return UserDTO.builder().userNo(user.getUserNo()).userName(user.getUserName()).email(user.getEmail())
				.phone(user.getPhone()).gradeNo(user.getGradeNo()).mileage(user.getMileage()).enabled(user.getEnabled())
				.build();
	}

	public User toEntity() {
		return User.builder().userNo(userNo).userName(userName).email(email).phone(phone).gradeNo(gradeNo)
				.mileage(mileage).enabled(enabled).build();
	}
}