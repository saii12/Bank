package com.tenco.bank.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.NaverProfile;
import com.tenco.bank.dto.OAuthToken;
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
		
		System.out.println("dto : " + dto.toString());
		System.out.println(dto.getCustomFile().getOriginalFilename()); //  getOriginalFilename()은 MultipartFile의 속성
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
		
		// 파일 업로드
		MultipartFile file = dto.getCustomFile();
		if(file.isEmpty() == false) {
			// 사용자가 이미지를 업로드했다면 기능구현
			// 파일 사이즈 체크
			// 20MB
			if(file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfulException("파일 크기는 20MB 이상 클 수 없습니다", HttpStatus.BAD_REQUEST);
			}
			
			// 서버 컴퓨터에 파일 넣을 디렉토리가 있는지 검사
			String saveDirectory = Define.UPLOAD_FILE_DIRECTORY;
			// 폴더가 없다면 오류 발생(파일 생성시)
			File dir = new File(saveDirectory);
			if(dir.exists() == false) {
				dir.mkdir(); // 폴더가 없으면 폴더 생성
			}
			
			// 파일 이름(중복처리 예방)
			UUID uuid = UUID.randomUUID();
			String fileName = uuid + "_" + file.getOriginalFilename();
			System.out.println("file Name : " + fileName);
			// C:\\work_spring\\upload\ab.png
			String uploadPath = Define.UPLOAD_FILE_DIRECTORY + File.separator + fileName; // File.separator는 \ 를 나타낸다
			System.out.println("uploadPath : " + uploadPath);
			File destination = new File(uploadPath);
			
			try {
				file.transferTo(destination);
			} catch (IllegalStateException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 객체 상태 변경
			dto.setOriginFileName(file.getOriginalFilename()); // ex) bag.jpeg
			dto.setUploadFileName(fileName); // ex) 8f4b467e-3a72-41d6-ac9c-ed2a00b6b5eb_bag.jpeg
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
	
	
	// http://localhost:80/user/kakao-callback?code="xxxxxxx"
	@GetMapping("/kakao-callback")
	//@ResponseBody // <-- 데이터를 반환
	public String kakaoCallback(@RequestParam String code) {
		System.out.println("code : " + code);
		
		// POST 방식, Header 구성, body 구성
		RestTemplate rt1 = new RestTemplate();
		// 헤더 구성
		HttpHeaders headers1 = new HttpHeaders();
		headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); // key, value
		// body 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "ce1e33f8406e67989901a6130e4ff70b");
		params.add("redirect_uri", "http://localhost:80/user/kakao-callback");
		params.add("code", code);
		
		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> reqMsg = new HttpEntity<>(params, headers1);
		
		ResponseEntity<OAuthToken> response = rt1.exchange("https://kauth.kakao.com/oauth/token", 
				HttpMethod.POST, reqMsg, OAuthToken.class);
		
		
		// 다시 요청 -- 인증토큰 -- 사용자 정보 요청
		RestTemplate rt2 = new RestTemplate();
		
		// 헤더
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + response.getBody().getAccessToken()); //response.getBody()까지는 OAuthToken Object임
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 바디 X
		
		// 결합 --> 요청
		HttpEntity<MultiValueMap<String, String>> kakaoInfo = new HttpEntity<>(headers2);
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", 
						HttpMethod.POST, kakaoInfo, KakaoProfile.class);
		
		System.out.println(response2.getBody());
		
		KakaoProfile kakaoProfile = response2.getBody();
		
		
		// 최초 사용자 판단 여부 -- 사용자 username 존재 여부 확인
		// 우리사이트 --> 카카오
		SignUpFormDTO dto = SignUpFormDTO.builder()
				.username("OAuth_" + kakaoProfile.getProperties().getNickname())
				.fullname("Kakao")
				.password("asd1234")
				.build();
		
		// 최초 사용자인지 username으로 확인
		User oldUser = userService.readUserByUserName(dto.getUsername());
		// null <--
		if(oldUser == null) {
			userService.createUser(dto);
			//////////////////////////////
			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
		}
		oldUser.setPassword(null); // oldUser의 password 왜 null로 하는거였지?? 보안상 노출되지 않기 위해
		// 로그인 처리
		httpSession.setAttribute(Define.PRINCIPAL, oldUser);
		
		// 단 최소 요청 사용자라 --> 회원 후 로그인처리
		
		// DTO 설계하기
		return "redirect:/account/list";
	}
	
	
	// 네이버 소셜 로그인
	@GetMapping("/naver-callback")
	public String naverCallback(@RequestParam String code, @RequestParam String state) {
		
		RestTemplate rt = new RestTemplate();
		
		URI uri = UriComponentsBuilder.fromUriString("https://nid.naver.com").path("/oauth2.0/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", "KDwJYTBj0wO0YT2VBsoQ")
				.queryParam("client_secret", "Fvex_yZ44z")
				.queryParam("code", code)
				.queryParam("state", state).encode()
				.build().toUri();
		
		ResponseEntity<NaverProfile> response = rt.getForEntity(uri, NaverProfile.class);
		
		RestTemplate rt2 = new RestTemplate();
		HttpHeaders headers2 = new HttpHeaders();
		String token = "AAAAO6SKIpbziPlIOJNZRBxQlSF1huH632_OZnFVZuemyzgVK9Q9B1sLHMjdgS8V1g0v6nYo4L-EeXtKFFh6-z-xKww";
		headers2.add("Authorization", "Bearer " + token);
		
		HttpEntity<MultiValueMap<String, String>> naverInfo = new HttpEntity<>(headers2);
		
		ResponseEntity<NaverProfile> response2 = rt2.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.POST, naverInfo, NaverProfile.class);
		
		NaverProfile naverProfile = response2.getBody();
		
		SignUpFormDTO dto = SignUpFormDTO.builder().username("Naver_" + naverProfile.getNickname())
				.fullname("Naver")
				.password("asd1234")
				.build();
		
		User oldUser = userService.readUserByUserName(dto.getUsername());
		
		if(oldUser == null) {
			userService.createUser(dto);
			oldUser = new User();
			oldUser.setUsername(dto.getUsername());
			oldUser.setFullname(dto.getFullname());
			
		}
		
		oldUser.setPassword(null);
		
		httpSession.setAttribute(Define.PRINCIPAL, oldUser);
		
		
		
		return "redirect:/account/list";
	}

}
