package com.tenco.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.AccountSaveFormDTO;
import com.tenco.bank.dto.depositFormDTO;
import com.tenco.bank.dto.transferFormDTO;
import com.tenco.bank.dto.withdrawFormDTO;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.entity.Account;
import com.tenco.bank.repository.entity.CustomHistoryEntity;
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

	/*
	 * public AccountController(HttpSession session, AccountService service) {
	 * this.session = session; this.accountService = service; }
	 */

	// 페이지 요청
	// http://localhost:80/account/save // ?붙으면 쿼리파라미터
	/**
	 * 계좌 생성 페이지 요청
	 * 
	 * @return saveForm.jsp
	 */
	@GetMapping("/save")
	public String savePage() {

//		// 인증검사
//		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}
		return "account/saveForm";
	}

	// 계좌 생성 로직만들기
	/**
	 * 계좌 생성 처리
	 * 
	 * @param dto
	 * @return list.jsp
	 */
	@PostMapping("/save")
	public String saveProc(AccountSaveFormDTO dto) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		// 2. 유효성 검사
		if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (dto.getBalance() == null || dto.getBalance() < 0) {
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
	 * 
	 * @param model - accountList
	 * @return list.jsp
	 */
	@GetMapping({ "/list", "/" })
	public String listPage(Model model) {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		// 경우의 수 유, 무
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());

		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}

		return "account/list";
	}

	// 출금 페이지 요청
	// http://localhost:80/account/withdraw
	@GetMapping("/withdraw")
	public String withdrawPage() {
//		// 1. 인증검사
//		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		return "account/withdraw";
	}

	// 출금 요청 로직 만들기
	@PostMapping("/withdraw")
	public String withdrawProc(withdrawFormDTO dto) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		// 2. 유효성 검사
		if (dto.getAmount() == null) {
			throw new CustomRestfulException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountPassword() == null || dto.getWAccountPassword().isEmpty()) {
			throw new CustomRestfulException("출금계좌 비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		accountService.updateAccountWithdraw(dto, principal.getId());

		return "redirect:/account/list";

	}

	// 입금페이지 요청
	@GetMapping("/deposit")
	public String depositPage() {

//		// 1. 인증검사
//		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		return "account/deposit";
	}

	// 입금 요청 로직 만들기
	@PostMapping("/deposit")
	public String depositProc(depositFormDTO dto) {

		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		// 2. 유효성검사
		if (dto.getAmount() == null) { // isempty 왜 안해? Long타입이라서 그런거래
			throw new CustomRestfulException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() <= 0) { // longValue?
			throw new CustomRestfulException("잘못된 금액입니다", HttpStatus.BAD_REQUEST);
		}

		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountDeposit(dto, principal.getId());

		return "redirect:/account/list";
	}

	// 이체 페이지 요청
	@GetMapping("/transfer")
	public String transferPage() {
//		// 1. 인증검사
//		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}

		return "account/transfer";
	}

	// 이체 요청 로직 만들기
	@PostMapping("/transfer")
	public String transferProc(transferFormDTO dto) {
		// 1. 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
//		}
		
		// 2. 유효성검사
		if (dto.getAmount() == null) { // isempty 왜 안해? Long타입이라서 그런거래
			throw new CustomRestfulException("금액을 입력하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getAmount().longValue() <= 0) { // longValue?
			throw new CustomRestfulException("잘못된 금액입니다", HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().isEmpty()) {
			throw new CustomRestfulException("출금 계좌번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new CustomRestfulException("계좌 비밀번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().isEmpty()) {
			throw new CustomRestfulException("입금 계좌번호를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		accountService.updateAccountTransfer(dto, principal.getId());
		
		return "redirect:/account/list";
	}
	
	// 계좌 상세 보기 페이지 -- 전체(입출금), 입금, 출금
	// http://localhost:80/account/detail/1
	// {id} 파라미터 받으려면 PathVariable 어노테이션 사용
	// RequestParam에서 required = false 해야 type 파라미터 값 없을 때도 기본값"all"로 인식함(true가 기본설정임)
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Integer id, 
			@RequestParam(name = "type", defaultValue = "all", required = false) String type, 
			Model model) {
		
		//System.out.println("type : " + type);
		
		// 인증검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL); // Object 타입을 다운캐스팅
//		if (principal == null) {
//			throw new UnAuthorizedException(Define.ENTER_YOUR_LOGIN, HttpStatus.UNAUTHORIZED);
//		}
		
		Account account = accountService.readByAccountId(id);
		
		// 서비스 호출
		List<CustomHistoryEntity> historyList = accountService.readHistoryListByAccount(type, id);
		System.out.println("list : " + historyList.toString());
		
		model.addAttribute("account", account);
		model.addAttribute("historyList", historyList);
		model.addAttribute("principal", principal);
		
		
		// 응답 결과물 --> jsp 내려주기
		return "account/detail";
	}
}
