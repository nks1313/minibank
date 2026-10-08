package com.study.minibank.account.service;

import com.study.minibank.account.domain.Account;
import com.study.minibank.account.domain.AccountTransaction;
import com.study.minibank.account.domain.TransactionType;
import com.study.minibank.account.dto.AccountResponse;
import com.study.minibank.account.dto.TransactionResponse;
import com.study.minibank.account.repository.AccountRepository;
import com.study.minibank.account.repository.AccountTransactionRepository;
import com.study.minibank.common.exception.BusinessException;
import com.study.minibank.common.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 계좌 업무 처리.
 *
 * @Transactional: 메서드 시작 시 트랜잭션 BEGIN, 정상 종료 시 COMMIT,
 *                 RuntimeException이 밖으로 던져지면 ROLLBACK.
 * 클래스에 readOnly=true를 기본으로 두고, 데이터를 바꾸는 메서드에만 @Transactional을 다시 붙인다.
 */
@Service
@RequiredArgsConstructor // final 필드를 받는 생성자 자동 생성 → Spring이 의존 객체를 주입(DI)
@Transactional(readOnly = true)
public class AccountService {

	private static final int MAX_ACCOUNT_NUMBER_RETRY = 5;

	private final AccountRepository accountRepository;
	private final AccountTransactionRepository transactionRepository;
	private final AccountNumberGenerator accountNumberGenerator;

	@Transactional
	public AccountResponse open(String ownerName) {
		Account account = Account.open(generateUniqueAccountNumber(), ownerName);
		accountRepository.save(account);
		return AccountResponse.from(account);
	}

	public AccountResponse getAccount(String accountNumber) {
		return AccountResponse.from(findAccount(accountNumber));
	}

	@Transactional
	public TransactionResponse deposit(String accountNumber, BigDecimal amount, String description) {
		Account account = findAccount(accountNumber);
		account.deposit(amount);
		// account를 다시 save()하지 않아도 된다: 커밋 시 JPA가 변경을 감지(dirty checking)해 UPDATE를 실행
		AccountTransaction tx = transactionRepository.save(
				AccountTransaction.record(account, TransactionType.DEPOSIT, amount, description));
		return TransactionResponse.from(tx);
	}

	@Transactional
	public TransactionResponse withdraw(String accountNumber, BigDecimal amount, String description) {
		Account account = findAccount(accountNumber);
		account.withdraw(amount); // 잔액 부족이면 여기서 예외 → 롤백, 원장도 남지 않음
		AccountTransaction tx = transactionRepository.save(
				AccountTransaction.record(account, TransactionType.WITHDRAW, amount, description));
		return TransactionResponse.from(tx);
	}

	public List<TransactionResponse> getTransactions(String accountNumber) {
		Account account = findAccount(accountNumber);
		return transactionRepository.findAllByAccountOrderByIdDesc(account).stream()
				.map(TransactionResponse::from)
				.toList();
	}

	private Account findAccount(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND));
	}

	private String generateUniqueAccountNumber() {
		for (int i = 0; i < MAX_ACCOUNT_NUMBER_RETRY; i++) {
			String candidate = accountNumberGenerator.generate();
			if (!accountRepository.existsByAccountNumber(candidate)) {
				return candidate;
			}
		}
		throw new BusinessException(ErrorCode.ACCOUNT_NUMBER_GENERATION_FAILED);
	}
}
