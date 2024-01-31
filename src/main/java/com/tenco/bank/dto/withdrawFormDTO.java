package com.tenco.bank.dto;

import lombok.Data;

@Data
public class withdrawFormDTO {

	private Long amount;
	private String wAccountNumber; // 왜 String으로 하는거임? / DB타입 따라
	private String wAccountPassword;
}
