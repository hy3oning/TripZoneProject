package com.kh.trip.dto;

import jakarta.validation.constraints.NotBlank;

public class RestoreRequestDTO {
	// loginId 또는 email 입력
	@NotBlank(message = "아이디 또는 이메일을 입력해주세요.")
	private String identifier;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
}
