package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.MemberGrade;

public interface MemberGradeRepository extends JpaRepository<MemberGrade, Long>{
	
}
