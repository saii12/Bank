package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomPageException extends RuntimeException{

	// 상태코드
	private HttpStatus httpStatus;
	
	//ctrl space enter
	public CustomPageException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
	// 사용하는 시점에 활용 가능
	//new CustomPageException("바보야", HttpStatus.ok);
	
	
}
