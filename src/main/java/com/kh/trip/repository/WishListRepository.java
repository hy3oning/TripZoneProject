package com.kh.trip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kh.trip.domain.WishList;

@Repository

public interface WishListRepository extends JpaRepository<WishList, Long> {

	static Page<Object[]> selectList(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
}