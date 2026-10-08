package com.study.minibank.account.domain;

import com.study.minibank.common.exception.BusinessException;
import com.study.minibank.common.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 계좌 엔티티. account 테이블의 한 행(row)과 1:1로 대응된다.
 *
 * 잔액 변경 규칙(입금/출금)은 Service가 아니라 엔티티 안에 둔다.
 * → balance 필드를 바깥에서 직접 바꿀 수 없으므로(setter 없음) 규칙을 우회할 수 없다.
 */
@Entity
@Table(name = "account")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 내부적으로 쓰는 기본 생성자
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 auto increment 사용
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String accountNumber;

	@Column(nullable = false, length = 50)
	private String ownerName;

	/** 원화 기준이므로 소수점 없이 저장. 금액은 절대 double/float를 쓰지 않는다. */
	@Column(nullable = false, precision = 19, scale = 0)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING) // DB에 0,1 대신 "ACTIVE" 같은 문자열로 저장
	@Column(nullable = false, length = 20)
	private AccountStatus status;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	/** 신규 계좌 개설 (생성자 대신 이름 있는 정적 메서드 사용) */
	public static Account open(String accountNumber, String ownerName) {
		Account account = new Account();
		account.accountNumber = accountNumber;
		account.ownerName = ownerName;
		account.balance = BigDecimal.ZERO;
		account.status = AccountStatus.ACTIVE;
		return account;
	}

	public void deposit(BigDecimal amount) {
		validateActive();
		validateAmount(amount);
		this.balance = this.balance.add(amount);
	}

	public void withdraw(BigDecimal amount) {
		validateActive();
		validateAmount(amount);
		if (this.balance.compareTo(amount) < 0) { // BigDecimal 비교는 <, > 대신 compareTo 사용
			throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
		}
		this.balance = this.balance.subtract(amount);
	}

	private void validateActive() {
		if (this.status != AccountStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE);
		}
	}

	private static void validateAmount(BigDecimal amount) {
		if (amount == null || amount.signum() <= 0) {
			throw new BusinessException(ErrorCode.INVALID_AMOUNT);
		}
	}
}
