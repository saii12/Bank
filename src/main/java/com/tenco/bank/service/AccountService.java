package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.AccountSaveFormDTO;
import com.tenco.bank.dto.depositFormDTO;
import com.tenco.bank.dto.transferFormDTO;
import com.tenco.bank.dto.withdrawFormDTO;
import com.tenco.bank.handler.exception.CustomRestfulException;
import com.tenco.bank.repository.entity.Account;
import com.tenco.bank.repository.entity.History;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.utils.Define;

@Service // IoC 대상 + 싱글톤으로 관리됨
public class AccountService {

	// SOLID 원칙
	// OCP
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private HistoryRepository historyRepository;

	// 계좌 생성
	// 사용자 정보 필요
	// todo 계좌번호 중복확인 예정
	@Transactional
	public void createAccount(AccountSaveFormDTO dto, Integer principalId) {

		// 계좌 번호 중복확인
		// 예외 처리
		if (readAccount(dto.getNumber()) != null) {
			throw new CustomRestfulException(Define.EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 여기선 Builder 안 쓰네
		Account account = new Account();
		account.setNumber(dto.getNumber());
		account.setPassword(dto.getPassword());
		account.setBalance(dto.getBalance());
		account.setUserId(principalId);

		int resultRowCount = accountRepository.insert(account);

		if (resultRowCount != 1) {
			throw new CustomRestfulException(Define.FAIL_TO_CREATE_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 단일 계좌 검색 기능
	public Account readAccount(String number) {

		return accountRepository.findByNumber(number.trim());
	}

	// 계좌목록 보기 기능
	public List<Account> readAccountListByUserId(Integer principalId) {

		return accountRepository.findAllByUserId(principalId);
	}

	// 출금기능 만들기
	// 1. 계좌 존재 여부 확인 -- select
	// 2. 본인 계좌 여부 확인 -- 객체에서 확인처리
	// 3. 계좌 비번 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 ---> update
	// 6. 거래 내역 등록 --> insert
	// 7. 트랜잭션 처리(기능 동작 중 혹시나 오류나면 전체 기능 전부 진행되면 안되므로)
	@Transactional
	public void updateAccountWithdraw(withdrawFormDTO dto, Integer principalId) {

		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if (accountEntity == null) {
			throw new CustomRestfulException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 2
		accountEntity.checkOwner(principalId);

//		if(accountEntity.getUserId() != principalId) {
//			throw new CustomRestfulException("본인 소유 계좌가 아닙니다", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		// 3 (String) 불변
		accountEntity.checkPassword(dto.getWAccountPassword());

//		//									false							
//		if(accountEntity.getPassword().equals(dto.getWAccountPassword()) == false){
//			throw new CustomRestfulException("출금계좌 비밀번호가 틀렸습니다", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		// 4
		accountEntity.checkBalance(dto.getAmount());

//		if(accountEntity.getBalance() < dto.getAmount()) {
//			throw new CustomRestfulException("계좌잔액이 부족합니다", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		// 5 --> 출금 기능(Account) --> 객체 상태값 변경
		accountEntity.withdraw(dto.getAmount());
		accountRepository.updateById(accountEntity);
		// 6 --> 거래내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);

		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new CustomRestfulException("정상 처리되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 입금기능 만들기
	@Transactional
	public void updateAccountDeposit(depositFormDTO dto, Integer id) {

		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());

		if (accountEntity == null) {
			throw new CustomRestfulException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 본인계좌 확인?

		accountEntity.deposit(dto.getAmount());
		accountRepository.updateById(accountEntity);

		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());

		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new CustomRestfulException("정상 처리되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 이체 기능 만들기
	// 1. 출금 계좌 존재 여부
	// 2. 입금 계좌 존재 여부
	// 3. 출금 계좌 본인 소유 확인
	// 4. 출금 계좌 비번 확인
	// 5. 출금 계좌 잔액 확인
	// 6. 출금 계좌 잔액 수정
	// 7. 입금 계좌 잔액 수정
	// 8. 거래 내역 등록 처리
	// 9. 트랜잭션 처리
	@Transactional
	public void updateAccountTransfer(transferFormDTO dto, Integer id) {

		Account wAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		Account dAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		
		if(wAccountEntity == null) {
			throw new CustomRestfulException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(dAccountEntity == null) {
			throw new CustomRestfulException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		wAccountEntity.checkOwner(id);
		wAccountEntity.checkPassword(dto.getPassword());
		wAccountEntity.checkBalance(dto.getAmount());
		
		wAccountEntity.withdraw(dto.getAmount());
		accountRepository.updateById(wAccountEntity);
		
		dAccountEntity.deposit(dto.getAmount());
		accountRepository.updateById(dAccountEntity);
		
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(wAccountEntity.getBalance());
		history.setDBalance(dAccountEntity.getBalance());
		history.setWAccountId(wAccountEntity.getId());
		history.setDAccountId(dAccountEntity.getId());
		
		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new CustomRestfulException("정상 처리되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
