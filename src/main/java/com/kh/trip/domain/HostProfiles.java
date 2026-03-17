package com.kh.trip.domain;

import java.util.List;
import java.util.ArrayList;

import com.kh.trip.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "HOST_PROFILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HostProfiles extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_host_profile")
	@SequenceGenerator(name = "seq_host_profile", sequenceName = "SEQ_HOST_PROFILE", allocationSize = 1)
	@Column(name = "HOST_NO")
	private Long hostNo;

	@Column(name = "USER_NO", nullable = false, unique = true)
	private Long userNo;

	@Column(name = "BUSINESS_NAME", nullable = false, length = 100)
	private String businessName;

	@Column(name = "BUSINESS_NUMBER", nullable = false, length = 50, unique = true)
	private String businessNumber;

	@Column(name = "OWNER_NAME", nullable = false, length = 100)
	private String ownerName;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@Builder.Default
	private List<ApprovalStatus> approvalStatusList = new ArrayList<>();
	
	public void addStatus(ApprovalStatus approvalStatus) {
		approvalStatusList.add(approvalStatus);
	}
	

	@Column(name = "APPROVED_BY")
	private Long approvedBy;

	@Column(name = "APPROVED_AT")
	private java.util.Date approvedAt;

	@Column(name = "REJECT_REASON", length = 300)
	private String rejectReason;

}