package com.kh.trip.service;

import com.kh.trip.dto.AdminUserSearchRequestDTO;
import com.kh.trip.dto.PasswordChangeRequestDTO;
import com.kh.trip.dto.PageResponseDTO;
import com.kh.trip.dto.UserDTO;
import com.kh.trip.dto.UserUpdateRequestDTO;

public interface UserService {
	
	UserDTO getUser(Long userNo);
	void update(Long userNo, UserUpdateRequestDTO request);
	void changePassword(Long userNo, PasswordChangeRequestDTO request);
	void delete(Long UserNo);
	void restore(Long userNo);
	PageResponseDTO<UserDTO> findUsers(AdminUserSearchRequestDTO request);
}
