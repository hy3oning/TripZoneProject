package com.kh.trip.service.auth;

import com.kh.trip.dto.auth.LoginRequestDTO;
import com.kh.trip.dto.auth.LoginResponseDTO;
import com.kh.trip.dto.auth.LogoutRequestDTO;
import com.kh.trip.dto.auth.RefreshTokenRequestDTO;
import com.kh.trip.dto.auth.RegisterRequestDTO;
import com.kh.trip.dto.auth.TokenRefreshResponseDTO;
import com.kh.trip.dto.auth.social.GoogleLoginRequestDTO;
import com.kh.trip.dto.auth.social.KakaoLoginRequestDTO;
import com.kh.trip.dto.auth.social.NaverLoginRequestDTO;

public interface AuthService {

	void register(RegisterRequestDTO request);

	LoginResponseDTO login(LoginRequestDTO request);

	void logout(LogoutRequestDTO request);

	TokenRefreshResponseDTO refresh(RefreshTokenRequestDTO request);

	LoginResponseDTO googleLogin(GoogleLoginRequestDTO request);

	LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO request);

	LoginResponseDTO naverLogin(NaverLoginRequestDTO request);
}
