package com.tenco.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.AccountSaveFormDTO;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.entity.Account;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired // 가독성(생성자랑 둘 다 있으면 생성자 타지만, DI했다 쉽게 알려주기 위해)
	private HttpSession session; // 생성자 쓸땐 final 쓰기(이유 뭐였더라?)
	
	@Autowired
	private AccountService accountService;
	
	/*public AccountController(HttpSession session, AccountService service) {
		this.session = session;
		this.accountService = service;
	}
	*/
	
	// 페이지 요청
	// http://localhost:80/account/save  // ?붙으면 쿼리파라미터
	/**
	 * 계좌 생성 페이지 요청
	 * @return saveForm.jsp
	 */
	@GetMapping("/save")
	public String savePage() {
		
		// 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
		if(principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "account/saveForm";
	}
	
	// 계좌 생성 로직만들기
	/**
	 * 계좌 생성 처리
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/save")
	public String saveProc(AccountSaveFormDTO dto) {
		
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
		if(principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		// 2. 유효성 검사
		if(dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if(dto.getBalance() == null || dto.getBalance() < 0) {
			throw new CustomRestfulException("잘못된 금액입니다", HttpStatus.BAD_REQUEST);
		}
		
		// 3. 서비스 호출
		accountService.createAccount(dto, principal.getId());
		
		// 4. 응답 처리
		return "redirect:/account/list";
	}
	
	// 계좌 목록 보기 페이지 생성
	// http://localhost:80/account/list or http://localhost:80/account/
	/**
	 * 계좌목록 페이지
	 * @param model - accountList
	 * @return list.jsp
	 */
	@GetMapping({"/list", "/"})
	public String listPage(Model model) {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
		if(principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		
		// 경우의 수 유, 무
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());
		
		if(accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}
		
		return"account/list";
	}
	
}
