package com.tenco.bank.repository.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ctrl shift o 는 필요없는 코드 삭제

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	// 기본 생성자
	// 사용자 정의 생성자
	//public User() {}
	
	private Integer id;
	private String username;
	private String password;
	private String fullname;
	private Timestamp createdAt;
	
	private String originFileName;
	private String uploadFileName;

	// 사용자가 회원가입시 이미지 존재, 이미지 존재X 에 따라 이미지 경로 선택하기
	public String setupUserImage() {
		return uploadFileName == null ? 
				"https://picsum.photos/id/1/350" : "/images/upload/" + uploadFileName;
	}
}
