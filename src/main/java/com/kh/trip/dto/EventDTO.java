package com.kh.trip.dto;


import java.time.LocalDateTime;
import java.util.List;

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
public class EventDTO {
	private Long eventNo;
	private Long adminUserNo;
    private String title;
    private String content;
    private String thumbnailUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long viewCount;
    private List<Long> coupons;
    private List<String> couponNames;
}
