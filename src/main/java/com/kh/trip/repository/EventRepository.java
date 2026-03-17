package com.kh.trip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

	static Page<Object[]> selectList(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
