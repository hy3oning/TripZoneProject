package com.kh.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDTO {

	@NotBlank
	private String currentPassword;

	@NotBlank
	private String newPassword;
}
