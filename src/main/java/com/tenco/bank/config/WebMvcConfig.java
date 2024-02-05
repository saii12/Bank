package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

// @Configuration --> 스프링부트 설정 클래스이다.
@Configuration // IoC 대상 : 2개 이상에 IoC(Bean) 사용
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Autowired // DI
	private AuthInterceptor authInterceptor;

	// 요청 올때마다 domain URI 검사할 예정
	// /account/xxx <- 으로 들어오는 도메인을 다 검사해!!! / 도메이란? 범주화
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/account/**")
				.addPathPatterns("/auth/**");
	}
	
	@Bean // IoC 대상 - 싱글톤 처리
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// 리소스 등록처리
	// 서버 컴퓨터에 위치한 리소스를 활용하는 방법(프로젝트 외부 폴더 접근)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 가짜 경로 <--
		registry.addResourceHandler("/images/upload/**") // 이런 경로로 지정되어 있다면
		.addResourceLocations("file:///C:\\work_spring\\upload/"); // 이 경로로 변경한다!
	}
}
