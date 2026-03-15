package com.kh.trip.domain;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_users")
	@SequenceGenerator(name = "seq_users", sequenceName = "SEQ_USERS", allocationSize = 1)
	@Column(name = "USER_NO")
	private Long userNo;

	@Column(name = "USER_NAME", nullable = false, length = 100)
	private String userName;

	@Column(name = "EMAIL", nullable = false, length = 100, unique = true)
	private String email;

	@Column(name = "PHONE", nullable = false, length = 20)
	private String phone;

	@Column(name = "GRADE_NO")
	private Long gradeNo;

	@Builder.Default
	@Column(name = "MILEAGE", nullable = false)
	private Long mileage = 0L;

	@Builder.Default
	@Column(name = "ENABLED", nullable = false, length = 1)
	private String enabled = "1";
}
