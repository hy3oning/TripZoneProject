package com.kh.trip.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class AdminUserSearchRequestDTO extends PageRequestDTO {
	private String type; // name, email, all
	private String keyword; // 검색어
}
