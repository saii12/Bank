package com.tenco.bank.repository.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

	private Integer id;
	private String number;
	private String password;
	private Long balance;
	private Integer userId;
	private Timestamp createdAt;

	
	// 출금 기능
	// 함수의 선언부, 바디
	public void withdraw(Long amount) { // entity에서도 이렇게 메서드 구현할 수 있음
		// 방어적 코드 작성 - todo
		this.balance -= amount;
	}
	
	
	
	
	
	
	// 입금 기능
	public void deposit(Long amount) {
		this.balance += amount;
	}
	
	
	// 패스워드 체크
	// 잔액 여부 확인 기능
	// 계좌 소유자 확인 기능
}
