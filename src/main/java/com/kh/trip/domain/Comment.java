package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "COMMENT")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseTimeEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "SEQ_COMMENT")
	@SequenceGenerator(name = "SEQ_COMMENT",sequenceName = "SEQ_COMMENT",allocationSize = 1)
	@Column(name = "COMMENT_NO")
	private Long commentNo;
	
	//@ManyToOne // 외래키
	//@JoinColumn("INQUIRY_NO",nullable = false)
	//private Inquiry inquiry;
	
	@ManyToOne // 외래키
	@JoinColumn(name = "ADMIN_NO", nullable = false)
	private User user;
	
	@Column(name = "CONTENT",nullable = false)
	private String content;
	
	@Column(name = "STATUS", nullable = false)
	private boolean status;
	
	
	public void changeContent(String content) {
		this.content = content;
	}
	public void changeStatus(boolean status) {
		this.status = status;
	}
}
