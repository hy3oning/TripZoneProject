package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.UserCoupon;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>{
	
}
