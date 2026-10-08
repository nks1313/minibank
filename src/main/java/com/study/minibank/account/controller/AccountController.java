package com.study.minibank.account.controller;

import com.study.minibank.account.dto.AccountResponse;
import com.study.minibank.account.dto.MoneyRequest;
import com.study.minibank.account.dto.OpenAccountRequest;
import com.study.minibank.account.dto.TransactionResponse;
import com.study.minibank.account.service.AccountService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP 요청을 받아 Service에 넘기고, 결과를 JSON으로 돌려주는 계층.
 * 업무 로직은 두지 않는다. (전문 수신부 → 업무 모듈 호출 역할)
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	/** 계좌 개설 */
	@PostMapping
	public ResponseEntity<AccountResponse> open(@Valid @RequestBody OpenAccountRequest request) {
		AccountResponse response = accountService.open(request.ownerName());
		return ResponseEntity.created(URI.create("/api/accounts/" + response.accountNumber())).body(response);
	}

	/** 계좌 조회 */
	@GetMapping("/{accountNumber}")
	public AccountResponse getAccount(@PathVariable String accountNumber) {
		return accountService.getAccount(accountNumber);
	}

	/** 입금 */
	@PostMapping("/{accountNumber}/deposits")
	public TransactionResponse deposit(@PathVariable String accountNumber, @Valid @RequestBody MoneyRequest request) {
		return accountService.deposit(accountNumber, request.amount(), request.description());
	}

	/** 출금 */
	@PostMapping("/{accountNumber}/withdrawals")
	public TransactionResponse withdraw(@PathVariable String accountNumber, @Valid @RequestBody MoneyRequest request) {
		return accountService.withdraw(accountNumber, request.amount(), request.description());
	}

	/** 거래내역 조회 (최신순) */
	@GetMapping("/{accountNumber}/transactions")
	public List<TransactionResponse> getTransactions(@PathVariable String accountNumber) {
		return accountService.getTransactions(accountNumber);
	}
}
