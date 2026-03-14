package com.kh.trip.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_ROLES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_roles")
	@SequenceGenerator(name = "seq_user_roles", sequenceName = "SEQ_USER_ROLES", allocationSize = 1)
	@Column(name = "USER_ROLE_NO")
	private Long userRoleNo;

	@Column(name = "USER_NO", nullable = false)
	private Long userNo;

	@Column(name = "ROLE_CODE", nullable = false, length = 30)
	private String roleCode;

	@Column(name = "REG_DATE", nullable = false)
	private LocalDateTime regDate;

	@PrePersist
	public void prePersist() {
		if (this.regDate == null) {
			this.regDate = LocalDateTime.now();
		}
	}

}
