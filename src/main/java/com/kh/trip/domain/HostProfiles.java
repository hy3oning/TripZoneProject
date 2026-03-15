package com.kh.trip.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HOST_PROFILES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HostProfiles {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_host_profiles")
	@SequenceGenerator(name = "seq_host_profiles", sequenceName = "SEQ_HOST_PROFILES", allocationSize = 1)
	@Column(name = "HOST_NO")
	private Long hostNo; // 호스트 번호 (PK)

	@OneToOne // USER_NO는 UNIQUE 제약이 있으므로 1:1 관계
	@JoinColumn(name = "USER_NO", nullable = false, unique = true)
	private User user; // 회원 번호 (FK)

	@Column(name = "BUSINESS_NAME", nullable = false, length = 100)
	private String businessName; // 상호명

	@Column(name = "BUSINESS_NUMBER", nullable = false, length = 50, unique = true)
	private String businessNumber; // 사업자등록번호

	@Column(name = "OWNER_NAME", nullable = false, length = 100)
	private String ownerName; // 대표자명

	@Column(name = "APPROVAL_STATUS", nullable = false, length = 20)
	private String approvalStatus = "PENDING"; // 승인 상태 (기본값 PENDING)

	@ManyToOne // 여러 호스트 프로필을 한 관리자가 승인할 수 있음
	@JoinColumn(name = "APPROVED_BY")
	private User approvedBy; // 승인 처리한 관리자 회원 번호 (FK)

	@Column(name = "APPROVED_AT")
	private LocalDateTime approvedAt; // 승인 처리 일시

	@Column(name = "REJECT_REASON", length = 300)
	private String rejectReason; // 반려 사유

	@Column(name = "REG_DATE", nullable = false)
	private LocalDateTime regDate; // 등록일

	@Column(name = "UPD_DATE", nullable = false)
	private LocalDateTime updDate; // 수정일

	// ===== 엔티티 라이프사이클 콜백 =====
	@PrePersist
	public void prePersist() {
		this.regDate = LocalDateTime.now();
		this.updDate = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updDate = LocalDateTime.now();
	}
}