package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>{

}
