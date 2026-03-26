package com.kh.trip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.HostProfile;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.HostApprovalStatus;
import com.kh.trip.dto.HostProfileDTO;
import com.kh.trip.repository.HostProfileRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HostProfileServiceImpl implements HostProfileService {

	private final HostProfileRepository hostProfileRepository;
	private final UserRepository userRepository;

	@Override
	public Long register(HostProfileDTO hostProfileDTO) {
		HostProfile hostProfile = dtoToEntity(hostProfileDTO);
		HostProfile savedHostProfile = hostProfileRepository.save(hostProfile);
		return savedHostProfile.getHostNo();
	}

	@Override
	public List<HostProfileDTO> getList() {
		List<HostProfile> hostProfileList = hostProfileRepository.findAll();
		return hostProfileList.stream().map(this::entityToDTO).toList();
	}

	@Override
	public HostProfileDTO get(Long hostNo) {
		HostProfile hostProfile = hostProfileRepository.findById(hostNo)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호스트 프로필 입니다."));
		return entityToDTO(hostProfile);
	}

	private HostProfile dtoToEntity(HostProfileDTO hostProfileDTO) {
		User user = userRepository.findById(hostProfileDTO.getUserNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. userNo=" + hostProfileDTO.getUserNo()));

		return HostProfile.builder().user(user).businessName(hostProfileDTO.getBusinessName())
				.businessNumber(hostProfileDTO.getBusinessNumber()).ownerName(hostProfileDTO.getOwnerName())
				.approvalStatus(HostApprovalStatus.PENDING).build();
	}

	private HostProfileDTO entityToDTO(HostProfile hostProfile) {
		return HostProfileDTO.builder().hostNo(hostProfile.getHostNo()).userNo(hostProfile.getUser().getUserNo())
				.businessName(hostProfile.getBusinessName()).businessNumber(hostProfile.getBusinessNumber())
				.ownerName(hostProfile.getOwnerName()).approvalStatus(hostProfile.getApprovalStatus().name()).build();
	}

}
