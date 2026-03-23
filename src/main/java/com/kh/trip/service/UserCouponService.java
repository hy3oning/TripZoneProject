package com.kh.trip.service;

import com.kh.trip.dto.PageRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserCouponDTO;

public interface UserCouponService {

	Long save(UserCouponDTO userCouponDTO);

	PageResponseDTO<UserCouponDTO> findAll(Long userNo, PageRequestDTO pageRequestDTO);

}
