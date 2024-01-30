package com.tenco.bank.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data // =  getter setter 다른 곳에서 쓰기 위함
public class AccountSaveFormDTO {

	// Entity랑 똑같이 하는 게 아니다
	private String number;
	private String password;
	private Long balance;
}
