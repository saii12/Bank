package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

	// 회원가입
	// 화면을 반환
	// http://localhost:80/user/sign-up 하이폰 방식
	@GetMapping("/sign-up")
	public String signUpPage() {
		// prefix : /WEB-INF/view/
		// suffix: .jsp
		return "user/signUp";
	}
	
	
}
