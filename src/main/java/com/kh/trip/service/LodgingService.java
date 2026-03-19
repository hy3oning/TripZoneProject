package com.kh.trip.service;

import java.util.List;

import com.kh.trip.domain.Lodging;
import com.kh.trip.dto.LodgingDTO;
import com.kh.trip.dto.LodgingDetailDTO;

public interface LodgingService {

	// 숙소 등록
	public Lodging createLodging(Lodging lodging);

	// 숙소 단건 조회
	public Lodging getLodging(Long lodgingNo);

	// 숙소 전체 목록 조회
	public List<Lodging> getAllLodgings();

	// 지역으로 숙소 목록 조회
	public List<Lodging> getLodgingsByRegion(String region);

	// 숙소명 키워드 검색
	public List<Lodging> searchLodgingsByName(String keyword);

	// 숙소 수정
	public Lodging updateLodging(Long lodgingNo, LodgingDTO lodgingDTO);

	// 숙소 삭제
	public void deleteLodging(Long lodgingNo);
	
	//숙소 상세조회 
    LodgingDetailDTO getLodgingDetail(Long lodgingNo);

}
