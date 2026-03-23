package com.kh.trip.service;

import com.kh.trip.dto.UserDTO;
import com.kh.trip.dto.UserUpdateRequestDTO;

public interface UserService {
	
	UserDTO getUser(Long userNo);
	void delete(Long UserNo);
	void update(Long userNo, UserUpdateRequestDTO request);
}
