package com.kh.trip.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// 이메일로 회원 기본 정보를 조회한다.
	Optional<User> findByEmail(String email);

	// 이메일 중복 여부를 확인한다.
	boolean existsByEmail(String email);

}
