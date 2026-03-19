package com.kh.trip.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	// JWT 토큰을 검증하고, 토큰 안의 값을 꺼낼 때 사용한다.
	private final JwtProvider jwtProvider;

	// 토큰 안에서 꺼낸 loginId로 실제 사용자 정보를 다시 조회할 때 사용한다.
	private final CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 요청 헤더에서 Authorization 값을 꺼낸다.
		String authorizationHeader = request.getHeader("Authorization");

		// Authorization 헤더가 존재하고 Bearer 토큰 형식이면 JWT를 꺼낸다.
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);

			// 토큰이 위조되지 않았고, 만료되지 않았는지 확인한다.
			if (jwtProvider.validateToken(token)) {

				// 이 토큰이 ACCESS 토큰인지, REFRESH 토큰인지 확인한다.
				String tokenType = jwtProvider.getTokenType(token);

				// 보호된 API에는 ACCESS 토큰만 인증에 사용한다.
				if ("ACCESS".equals(tokenType)) {

					// 토큰의 subject에 저장된 loginId를 꺼낸다.
					String loginId = jwtProvider.getLoginId(token);

					// loginId로 DB에서 사용자 정보를 다시 조회한다.
					AuthUserPrincipal authUser = (AuthUserPrincipal) customUserDetailsService
							.loadUserByUsername(loginId);

					// 스프링 시큐리티가 사용할 인증 객체를 만든다.
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							authUser, null, authUser.getAuthorities());

					// 현재 요청의 부가정보(IP 등)를 인증 객체에 담는다.
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// 인증 객체를 SecurityContext에 저장한다.
					// 이 작업이 되어야 스프링 시큐리티가 "로그인된 사용자"로 인식한다.
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		// 현재 필터 작업이 끝났으니 다음 필터로 요청을 넘긴다.
		filterChain.doFilter(request, response);
	}

}
