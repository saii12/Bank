package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignInFormDTO;
import com.tenco.bank.dto.SignUpFormDTO;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired // DI 처리
	private UserService userService;
	
	@Autowired
	private HttpSession httpSession;

	
	
	// 회원가입
	// 화면을 반환
	// http://localhost:80/user/sign-up 하이폰 방식
	
	/**
	 * 회원가입 페이지 요청
	 * @return
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		// prefix : /WEB-INF/view/
		// suffix: .jsp
		return "user/signUp";
	}
	
	// 회원가입 요청 처리
	// http://localhost:80/user/sign-up
	/**
	 * 회원가입 요청
	 * @param dto
	 * @return 
	 */
	@PostMapping("/sign-up")
	public String signProc(SignUpFormDTO dto) {
		
		// 1. 인증 검사 x
		// 2. 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}

		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("password을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new CustomRestfulException("fullname을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		userService.createUser(dto);
		return "redirect:/user/sign-in"; //GET 방식 http://localhost:80/user/sign-in
	}
	
	// 1. 로그인페이지 요청처리 -- 페이지 요청
	// http://localhost:80/user/sign-in
	/**
	 * 로그인 페이지 요청
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		// 유효성 검사 x
		return "/user/signIn";
	}
	
	/**
	 * 로그인 요청처리
	 * @param SignInFormDTO
	 * @return account/list.jsp 
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDTO dto) {
		
		// 1. 인증 검사, 유효성 검사(인증 검사 먼저)
		// 인증 검사 x
		// 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new CustomRestfulException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("Password을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출예정
		User user = userService.readUser(dto);
		httpSession.setAttribute(Define.PRINCIPAL, user);
		
		// 로그인 완료 --> 페이지 결정 (account/list)
		return "redirect:/account/list";
	}
	
	// 로그아웃 기능 만들기
	@GetMapping("/logout")
	public String logout() {
		httpSession.invalidate();
		return "redirect:/user/sign-in";
	}
	
}
