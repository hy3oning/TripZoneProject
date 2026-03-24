package com.kh.trip.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.kh.trip.domain.User;
import com.kh.trip.dto.UserDTO;
import com.kh.trip.dto.UserUpdateRequestDTO;
import com.kh.trip.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDTO getUser(Long userNo) {
		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

		return entityToDTO(user);
	}

	private UserDTO entityToDTO(User user) {
		return UserDTO.builder().userNo(user.getUserNo()).userName(user.getUserName()).email(user.getEmail())
				.phone(user.getPhone())
				.gradeNo(user.getMemberGrade() != null ? user.getMemberGrade().getGradeNo() : null)
				.mileage(user.getMileage()).enabled(user.getEnabled()).build();
	}

	@Override
	public void delete(Long userNo) {
		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

		if ("0".equals(user.getEnabled())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 탈퇴 처리된 사용자입니다.");
		}
		user.changeEnabled("0");
		userRepository.save(user);
	}

	@Override
	public void update(Long userNo, UserUpdateRequestDTO request) {
		User user = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."

				));

		user.changePhone(request.getPhone());
		userRepository.save(user);
	}
}
