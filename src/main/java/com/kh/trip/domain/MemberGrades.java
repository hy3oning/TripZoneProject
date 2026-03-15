package com.kh.trip.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_GRADES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberGrades {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MEMBER_GRADES")
	@SequenceGenerator(name = "SEQ_MEMBER_GRADES", sequenceName = "SEQ_MEMBER_GRADES", allocationSize = 1)
	@Column(name = "grade_no")
	private Long gradeNo;
	
	@Column(name = "GRADE_NAME", nullable = false,  unique = true, length = 50)
	private String gradeName;
	
	@Column(name = "MIN_TOTAL_AMOUNT", nullable = false) //등급정렬에 쓰일 예정
	@Builder.Default
	@Min(0)
	private Long minTotalAmount = 0L;
	
	@Column(name = "MIN_STAY_COUNT", nullable = false)
	@Builder.Default
	@Min(0)
	private Long minStayCount = 0L;
	
	@Column(name = "MILEAGE_RATE", nullable = false)
	@Builder.Default
	@Min(0)
	private double mileageRate = 0.0; 
	
	@Column(name = "BENEFIT_DESC", length = 500)
	private String benefitDESC;
	
	@Column(name = "STATUS", nullable = false)
	@Builder.Default
	private boolean status = true; //삭제시 false
	
	@Column(name = "REG_DATE", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime regDate;
	
	@Column(name = "UPD_DATE", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updDate;
	
}
