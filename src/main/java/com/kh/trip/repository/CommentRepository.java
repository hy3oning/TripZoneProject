package com.kh.trip.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.trip.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	//findAll
	Page<Comment> findAll(Pageable pageable);

}
