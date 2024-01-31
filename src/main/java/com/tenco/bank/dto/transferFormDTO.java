package com.tenco.bank.dto;

import lombok.Data;

@Data
public class transferFormDTO {

	private Long amount;
	private String wAccountNumber;
	private String password;
	private String dAccountNumber;
}
