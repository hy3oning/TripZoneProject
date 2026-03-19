package com.kh.trip.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.kh.trip.domain.Coupon;
import com.kh.trip.domain.User;
import com.kh.trip.domain.enums.CouponStatus;
import com.kh.trip.dto.CouponDTO;
import com.kh.trip.repository.CouponRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
	private final CouponRepository repository;
	private final UserRepository userRepository;

	@Override
	public Long save(CouponDTO couponDTO) {
		// adminUserNo로 실제 DB에 있는 유저 탐색
		User user = userRepository.findById(couponDTO.getAdminUserNo())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자 번호입니다."));

		// 찾아온 '진짜' 유저 객체를 Coupon 엔티티에 저장
		Coupon coupon = Coupon.builder().user(user) 
				.couponName(couponDTO.getCouponName()).discountType(couponDTO.getDiscountType())
				.discountValue(couponDTO.getDiscountValue()).startDate(couponDTO.getStartDate())
				.endDate(couponDTO.getEndDate()).status(couponDTO.getStatus()).build();

		return repository.save(coupon).getCouponNo();
	}

	@Override
	public List<CouponDTO> findAll() {
		List<Coupon> result = repository.findAll();
		
		List<CouponDTO> dtoList = result.stream().map(coupon -> {
			CouponDTO dto = new CouponDTO();
			dto.setCouponNo(coupon.getCouponNo());
			dto.setCouponName(coupon.getCouponName());
			// 참조된 userno의 정확한 정보를 확인
			if (coupon.getUser() != null) {
				dto.setAdminUserNo(coupon.getUser().getUserNo());
			}

			dto.setDiscountType(coupon.getDiscountType());
			dto.setDiscountValue(coupon.getDiscountValue());
			dto.setStartDate(coupon.getStartDate());
			dto.setEndDate(coupon.getEndDate());
			dto.setStatus(coupon.getStatus());

			return dto;
		}).collect(Collectors.toList());
		 return dtoList;
	}

	@Override
	public void update(CouponDTO couponDTO) {
		Optional<Coupon> result = repository.findById(couponDTO.getCouponNo());
		Coupon coupon = result.orElseThrow();
		coupon.changeCouponName(couponDTO.getCouponName());
		coupon.changeDiscountType(couponDTO.getDiscountType());
		coupon.changeDiscountValue(couponDTO.getDiscountValue());
		coupon.changeStartDate(couponDTO.getStartDate());
		coupon.changeEndDate(couponDTO.getEndDate());
		repository.save(coupon);
	}

	@Override
	public void delete(Long couponNo) {
		Optional<Coupon> result = repository.findById(couponNo);
		Coupon coupon = result.orElseThrow();
		coupon.changeStatus(CouponStatus.DELETE);
		repository.save(coupon);
	}
	
}
