package com.kh.trip.service;

import java.util.List;

import com.kh.trip.dto.MemberGradeDTO;

public interface MemberGradeService {

	Long save(MemberGradeDTO memberGradeDTO);

	List<MemberGradeDTO> findAll();

	MemberGradeDTO findById(Long gradeNo);

	void delete(Long gradeNo);

	void update(MemberGradeDTO memberGradeDTO);

}
