package com.kh.trip.dto;

import jakarta.validation.constraints.NotBlank;
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
public class HostProfileDTO {

	private Long hostNo;
	private Long userNo;
	@NotBlank
	private String businessName;
	@NotBlank
	private String businessNumber;
	@NotBlank
	private String ownerName;
	private String approvalStatus;
}
