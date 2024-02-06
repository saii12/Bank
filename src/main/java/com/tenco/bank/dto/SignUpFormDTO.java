package com.tenco.bank.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpFormDTO {

	private String username;
	private String password;
	private String fullname;
	// 파일 처리
	private MultipartFile customFile; // name 속성값과 동일해야한다 / 이미지 여려개는 []
	
	private String originFileName;
	private String uploadFileName;
}
