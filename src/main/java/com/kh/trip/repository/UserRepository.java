package com.kh.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
