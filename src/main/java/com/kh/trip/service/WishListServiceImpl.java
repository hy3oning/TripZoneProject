package com.kh.trip.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.trip.domain.Lodging;
import com.kh.trip.domain.User;
import com.kh.trip.domain.WishList;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.WishListDTO;
import com.kh.trip.repository.LodgingRepository;
import com.kh.trip.repository.UserRepository;
import com.kh.trip.repository.WishListRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class WishListServiceImpl implements WishListService {
	
	private final WishListRepository wishListRepository;
	private final UserRepository userRepository;
	private final LodgingRepository lodgingRepository;
	
	//findAll(list)
	@Override
	public PageResponseDTO<WishListDTO> findAll(PageRequestDTO pageRequestDTO) {
		
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,pageRequestDTO.getSize(),
				Sort.by("wishListNo").descending());
		
		Page<WishList> result = wishListRepository.findAll(pageable);
		
		List<WishListDTO> dtoList = result.getContent().stream().map(wishList -> {
			return WishListDTO.builder().wishListNo(wishList.getWishListNo()).userNo(wishList.getUser().getUserNo())
					.lodgingNo(wishList.getLodging().getLodgingNo()).build();}).collect(Collectors.toList());
		
		long totalCount = result.getTotalElements();
		
		return PageResponseDTO.<WishListDTO>withAll().dtoList(dtoList)
				.totalCount(totalCount).pageRequestDTO(pageRequestDTO).build();
	}
	//save
	@Override
	@Transactional
	public Long save(WishListDTO wishListDTO) {
		log.info("..............");
		User user = userRepository.findById(wishListDTO.getUserNo()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자 입니다"));
		Lodging lodging = lodgingRepository.findById(wishListDTO.getLodgingNo()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 숙소 입니다"));
		WishList wishList = WishList.builder()
			    .user(user)       // .user(user) 대신 .userNo(user)로 시도
			    .lodging(lodging) // .lodging(lodging) 대신 .lodgingNo(lodging)로 시도
			    .build();
		
		WishList savedwishList = wishListRepository.save(wishList);
		return savedwishList.getWishListNo();
	}
	//delete
	@Override
	public void delete(Long wno) {
		wishListRepository.deleteById(wno);
		
	}
	
	
}
