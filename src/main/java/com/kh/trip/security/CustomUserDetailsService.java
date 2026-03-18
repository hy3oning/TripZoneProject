package com.kh.trip.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.trip.domain.User;
import com.kh.trip.domain.UserAuthProvider;
import com.kh.trip.domain.UserRole;
import com.kh.trip.repository.UserAuthProviderRepository;
import com.kh.trip.repository.UserRepository;
import com.kh.trip.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAuthProviderRepository userAuthProviderRepository;
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("username = [" + username + "]");
		// loginId로 인증 수단 정보를 조회한다.
		UserAuthProvider authProvider = userAuthProviderRepository.findByLoginId(username)
				.orElseThrow(() -> new UsernameNotFoundException("LoginId Not Found"));

		 // 로그인 정보 테이블에서 찾은 USER_NO를 이용해
        // 회원 기본 정보(이름, 이메일, 활성 여부 등)를 조회한다.
		User user = userRepository.findById(authProvider.getUserNo())
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

		// USER_NO로 회원 권한 목록을 조회한다.
		List<UserRole> userRoles = userRoleRepository.findByUserNo(user.getUserNo());

		 // 시큐리티가 사용할 수 있도록 권한 코드만 문자열 목록으로 변환한다.
		List<String> roleNames = userRoles.stream().map(UserRole::getRoleCode).toList();
		// 시큐리티에서 사용할 인증 객체로 변환한다.
		return new AuthUserPrincipal(user.getUserNo(), authProvider.getLoginId(), authProvider.getPasswordHash(),
				user.getUserName(), user.getEmail(), user.getPhone(), user.getEnabled(), roleNames);
	}
	
	

}
