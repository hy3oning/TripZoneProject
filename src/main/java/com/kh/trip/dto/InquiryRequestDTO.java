package com.kh.trip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryRequestDTO {

	@NotBlank
	private String inquiryType;

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	private Long userId;

	private Long senderUserId;	
}
