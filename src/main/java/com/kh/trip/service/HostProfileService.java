package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.HostProfileDTO;

public interface HostProfileService {

	Long register(HostProfileDTO hostProfileDTO);

	List<HostProfileDTO> getList();
	
	HostProfileDTO get(Long hostNo);
	
	void approve(Long hostNo, Long adminUserNo);
	
	void reject(Long hostNo, Long adminUserNo, String rejectReason);
}
