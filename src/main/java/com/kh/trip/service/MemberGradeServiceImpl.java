package com.kh.trip.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.MemberGrade;
import com.kh.trip.dto.MemberGradeDTO;
import com.kh.trip.repository.MemberGradeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberGradeServiceImpl implements MemberGradeService{
	
	private final ModelMapper modelMapper;
	private final MemberGradeRepository repository;

	@Override
	public Long save(MemberGradeDTO memberGradeDTO) {
		MemberGrade memberGrade = modelMapper.map(memberGradeDTO, MemberGrade.class);
		return repository.save(memberGrade).getGradeNo();
	}

	@Override
	public List<MemberGradeDTO> findAll() {
		List<MemberGrade> result = repository.findAll();
		List<MemberGradeDTO> dtoList = result.stream().map(entity -> modelMapper.map(entity, MemberGradeDTO.class))
				.collect(Collectors.toList());
		return dtoList;
	}

	@Override
	public MemberGradeDTO findById(Long gradeNo) {
		Optional<MemberGrade> result = repository.findById(gradeNo);
		MemberGrade grade = result.orElseThrow();
		MemberGradeDTO gradeDTO = modelMapper.map(grade, MemberGradeDTO.class);
		return gradeDTO;
	}

	@Override
	public void delete(Long gradeNo) {
		Optional<MemberGrade> result = repository.findById(gradeNo);
		MemberGrade grade = result.orElseThrow();
		grade.changeStatus(false);
		repository.save(grade);
	}

	@Override
	public void update(MemberGradeDTO gradeDTO) {
		Optional<MemberGrade> result = repository.findById(gradeDTO.getGradeNo());
		MemberGrade grade = result.orElseThrow();
		grade.changeName(gradeDTO.getGradeName());
		grade.changeMinAmount(gradeDTO.getMinTotalAmount());
		grade.changeMinStayCount(gradeDTO.getMinStayCount());
		grade.changeMileageRate(gradeDTO.getMileageRate());
		grade.changeBenefitDESC(gradeDTO.getBenefitDESC());
		repository.save(grade);
	}
	
	
}
