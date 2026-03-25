package com.kh.trip.dto;

import com.kh.trip.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
	private Long commentNo;
	private Long inquiryNo;
	private Long userNo;
	private String content;
	private boolean status;
}
