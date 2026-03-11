package com.kh.trip.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "NOTICE")
@SequenceGenerator(name = "NOTICE_SEQ_GEN", sequenceName = "NOTICE_SEQ", initialValue = 1, allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice {
	private int noticeNo;
	private int adminUserNo;
	private String title;
	private String content;
	private String noticeType;
	private boolean isPinned;
	private int viewCount;
	private String staus; //delete대신 hidden
	private Date regDate;
	private Date updDate;
}
