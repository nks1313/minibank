package com.study.minibank.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.minibank.account.domain.TransactionType;
import com.study.minibank.account.dto.AccountResponse;
import com.study.minibank.account.dto.TransactionResponse;
import com.study.minibank.account.repository.AccountRepository;
import com.study.minibank.account.repository.AccountTransactionRepository;
import com.study.minibank.common.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 통합 테스트: Spring과 H2 DB를 실제로 띄워서 Service → Repository → DB까지 검증한다.
 *
 * 테스트 메서드에 @Transactional을 붙이지 않았다.
 * → 서비스 메서드마다 실제로 커밋/롤백이 일어나므로, 롤백 후 DB 상태를 그대로 확인할 수 있다.
 */
@SpringBootTest
class AccountServiceTest {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountTransactionRepository transactionRepository;

	@AfterEach
	void cleanUp() {
		transactionRepository.deleteAllInBatch(); // FK 때문에 자식(원장)부터 삭제
		accountRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("입금하면 잔액이 늘고 거래원장에 입금 거래가 기록된다")
	void deposit() {
		String accountNumber = accountService.open("홍길동").accountNumber();

		TransactionResponse tx = accountService.deposit(accountNumber, new BigDecimal("50000"), "월급");

		assertThat(tx.type()).isEqualTo(TransactionType.DEPOSIT);
		assertThat(tx.balanceAfter()).isEqualByComparingTo("50000");
		assertThat(accountService.getAccount(accountNumber).balance()).isEqualByComparingTo("50000");
	}

	@Test
	@DisplayName("잔액 부족으로 출금이 실패하면 롤백되어 잔액도 원장도 변하지 않는다")
	void withdrawFailRollback() {
		String accountNumber = accountService.open("홍길동").accountNumber();
		accountService.deposit(accountNumber, new BigDecimal("10000"), null);

		assertThatThrownBy(() -> accountService.withdraw(accountNumber, new BigDecimal("20000"), null))
				.isInstanceOf(BusinessException.class);

		AccountResponse account = accountService.getAccount(accountNumber);
		assertThat(account.balance()).isEqualByComparingTo("10000");
		assertThat(accountService.getTransactions(accountNumber))
				.extracting(TransactionResponse::type)
				.containsExactly(TransactionType.DEPOSIT); // 출금 거래는 기록되지 않음
	}

	@Test
	@DisplayName("거래내역은 최신순으로 조회되고, 거래 후 잔액이 순서대로 기록된다")
	void transactions() {
		String accountNumber = accountService.open("홍길동").accountNumber();
		accountService.deposit(accountNumber, new BigDecimal("10000"), null);
		accountService.withdraw(accountNumber, new BigDecimal("3000"), null);
		accountService.deposit(accountNumber, new BigDecimal("500"), null);

		List<TransactionResponse> transactions = accountService.getTransactions(accountNumber);

		assertThat(transactions)
				.extracting(TransactionResponse::balanceAfter)
				.usingElementComparator(BigDecimal::compareTo)
				.containsExactly(new BigDecimal("7500"), new BigDecimal("7000"), new BigDecimal("10000"));
	}

	@Test
	@DisplayName("없는 계좌로 거래하면 예외가 발생한다")
	void accountNotFound() {
		assertThatThrownBy(() -> accountService.deposit("999999999999", BigDecimal.TEN, null))
				.isInstanceOf(BusinessException.class)
				.hasMessage("계좌를 찾을 수 없습니다.");
	}
}
