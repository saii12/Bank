package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // view -> data (@ResponseData)
//@RestController // data
@RequestMapping("/test1") // 대문
public class TestController {

	// 주소설계
	// http://localhost:80/test1/main
	@GetMapping("/main")
	public String mainPage() {
		System.out.println("11111");
		// 인증 검사(순서 1번)
		// 유효성 검사(2번)
		// 뷰 리졸브 --> 해당하는 파일 찾아 (data)
		//return "/WEB-INF/view/layout/main.jsp"; webapp 생략
		//prefix: /WEB-INF/view/
		//layout/main.jsp";
	    //suffix: .jsp 
		
		return "layout/main"; 
	}
}
