package com.kh.trip.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kh.trip.domain.Coupon;
import com.kh.trip.domain.User;
import com.kh.trip.domain.UserCoupon;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserCouponDTO;
import com.kh.trip.repository.CouponRepository;
import com.kh.trip.repository.UserCouponRepository;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService{

	private final UserCouponRepository repository;
	private final UserRepository userRepository;
	private final CouponRepository couponRepository;
	
	@Override
	public Long save(UserCouponDTO userCouponDTO) {
		User user = userRepository.findById(userCouponDTO.getUserNo())
				.orElseThrow(() -> new IllegalAccessError("존재하지 않는 관리자 번호입니다."));
		
		Coupon coupon = couponRepository.findById(userCouponDTO.getCouponNo())
				.orElseThrow(() -> new IllegalAccessError("존재하지 않는 쿠폰 번호입니다."));
		
		UserCoupon userCoupon = UserCoupon.builder().user(user).coupon(coupon)
				.issuedAt(userCouponDTO.getIssuedAt()).usedAt(userCouponDTO.getUsedAt())
				.status(userCouponDTO.getStatus()).build();
		
		return repository.save(userCoupon).getUserCouponNo();
	}

	@Override
	public PageResponseDTO<UserCouponDTO> findAll(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("userCouponNo").descending());
		
		Page<UserCoupon> result = repository.findAll(pageable);
		
		List<UserCouponDTO> dtoList = result.getContent().stream().map(userCoupon-> {
			return UserCouponDTO.builder().userCouponNo(userCoupon.getUserCouponNo())
					.userNo(userCoupon.getUser() != null? userCoupon.getUser().getUserNo() : null)
					.couponNo(userCoupon.getCoupon() != null? userCoupon.getCoupon().getCouponNo() : null)
					.issuedAt(userCoupon.getIssuedAt()).usedAt(userCoupon.getUsedAt()).status(userCoupon.getStatus())
					.build();
		}).collect(Collectors.toList());
		
		Long totalCount = result.getTotalElements();
		
		PageResponseDTO<UserCouponDTO> responseDTO = PageResponseDTO.<UserCouponDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();
		return responseDTO;
	}

}
