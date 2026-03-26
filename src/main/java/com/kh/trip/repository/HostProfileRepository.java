package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.HostProfile;

public interface HostProfileRepository extends JpaRepository<HostProfile, Long> {
	
	boolean existsByUser_UserNo(Long userNo);
	
	boolean existsByBusinessNumber(String businessNumber);
}
