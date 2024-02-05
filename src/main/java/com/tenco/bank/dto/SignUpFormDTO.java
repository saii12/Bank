package com.tenco.bank.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SignUpFormDTO {

	private String username;
	private String password;
	private String fullname;
	// 파일 처리
	private MultipartFile customFile; // name 속성값과 동일해야한다 / 이미지 여려개는 []
	
	private String originFileName;
	private String uploadFileName;
}
