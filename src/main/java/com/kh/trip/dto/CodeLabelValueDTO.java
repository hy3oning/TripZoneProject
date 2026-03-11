package com.kh.trip.dto;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeLabelValueDTO {
	private String value; // group_code (예: "A01")
	private String label; // group_name (예: "관리자")
}