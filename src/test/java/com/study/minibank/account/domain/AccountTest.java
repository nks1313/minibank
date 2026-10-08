package com.study.minibank.account.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.study.minibank.common.exception.BusinessException;
import com.study.minibank.common.exception.ErrorCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 순수 단위 테스트: Spring, DB 없이 엔티티의 업무 규칙만 검증한다. (실행 속도 매우 빠름)
 */
class AccountTest {

	@Test
	@DisplayName("신규 계좌는 잔액 0, 정상 상태로 개설된다")
	void open() {
		Account account = Account.open("110000000001", "홍길동");

		assertThat(account.getBalance()).isEqualByComparingTo("0");
		assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
	}

	@Test
	@DisplayName("입금하면 잔액이 증가한다")
	void deposit() {
		Account account = Account.open("110000000001", "홍길동");

		account.deposit(new BigDecimal("10000"));

		assertThat(account.getBalance()).isEqualByComparingTo("10000");
	}

	@Test
	@DisplayName("잔액 이하로 출금하면 잔액이 감소한다")
	void withdraw() {
		Account account = Account.open("110000000001", "홍길동");
		account.deposit(new BigDecimal("10000"));

		account.withdraw(new BigDecimal("10000"));

		assertThat(account.getBalance()).isEqualByComparingTo("0");
	}

	@Test
	@DisplayName("잔액보다 큰 금액을 출금하면 예외가 발생하고 잔액은 그대로다")
	void withdrawInsufficientBalance() {
		Account account = Account.open("110000000001", "홍길동");
		account.deposit(new BigDecimal("10000"));

		assertThatThrownBy(() -> account.withdraw(new BigDecimal("10001")))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode").isEqualTo(ErrorCode.INSUFFICIENT_BALANCE);
		assertThat(account.getBalance()).isEqualByComparingTo("10000");
	}

	@Test
	@DisplayName("0원 이하 금액은 거래할 수 없다")
	void invalidAmount() {
		Account account = Account.open("110000000001", "홍길동");

		assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode").isEqualTo(ErrorCode.INVALID_AMOUNT);
		assertThatThrownBy(() -> account.withdraw(new BigDecimal("-1")))
				.isInstanceOf(BusinessException.class)
				.extracting("errorCode").isEqualTo(ErrorCode.INVALID_AMOUNT);
	}
}
