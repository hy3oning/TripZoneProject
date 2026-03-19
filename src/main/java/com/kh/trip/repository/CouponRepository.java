package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
