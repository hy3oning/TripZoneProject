package com.kh.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kh.trip.domain.EventCoupon;

public interface EventCouponRepository extends JpaRepository<EventCoupon, Long>{
	
	@Query("select c from EventCoupon ec left join fetch ec.coupon c where ec.event.eventNo = :eno")
	List<EventCoupon> findAllById(Long eno);

	
}
