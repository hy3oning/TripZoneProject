package com.kh.trip.service;

import java.util.Map;

import com.kh.trip.domain.UserCoupon;
import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserCouponDTO;

public interface UserCouponService {

	Long save(UserCouponDTO userCouponDTO);

	PageResponseDTO<UserCouponDTO> findAll(PageRequestDTO pageRequestDTO);

}
