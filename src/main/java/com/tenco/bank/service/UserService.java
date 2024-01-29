package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInFormDTO;
import com.tenco.bank.dto.SignUpFormDTO;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.entity.User;
import com.tenco.bank.repository.interfaces.UserRepository;

@Service // IoC 대상
public class UserService {

	// 생성자 의존 주입 DI
	
	// DB 접근
	// @Autowired
	private UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	/**
	 * 회원가입 로직 처리
	 * @param signUpFormDTO
	 * return void
	 */
	// 회원가입
	@Transactional // 트랜잭션 처리 습관
	public void createUser(SignUpFormDTO dto) {
		
		User user = User.builder()
				.username(dto.getUsername())
				.password(dto.getPassword())
				.fullname(dto.getFullname())
				.build();
		
		int result = userRepository.insert(user);
		
		if(result != 1) {
			throw new CustomRestfulException("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 로그인 처리
	 * @param SignInFormDTO
	 * @return User
	 */
	// 로그인 처리 id 
	public User readUser(SignInFormDTO dto) {
		
		User user = User.builder()
				.username(dto.getUsername())
				.password(dto.getPassword())
				.build();
		
		User userEntity = userRepository.findByUsernameAndPassword(user);
		
		if(userEntity == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		
		return userEntity;
	}
}
