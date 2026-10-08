package com.study.minibank.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 거래원장. 한 번 기록되면 수정/삭제하지 않는다(INSERT only).
 * 잘못된 거래는 원거래를 고치는 대신 취소(반대) 거래를 새로 쌓는 방식으로 처리한다.
 */
@Entity
@Table(name = "account_transaction",
		indexes = @Index(name = "idx_account_transaction_account_id", columnList = "account_id, id"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 거래 일련번호 역할

	/** 여러 거래가 하나의 계좌에 속한다 (N:1). LAZY = 실제로 쓸 때 조회 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "account_id", nullable = false, updatable = false)
	private Account account;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, updatable = false)
	private TransactionType type;

	@Column(nullable = false, precision = 19, scale = 0, updatable = false)
	private BigDecimal amount;

	/** 거래 직후 잔액. 원장만 보고도 잔액 흐름을 검증할 수 있게 함께 기록한다. */
	@Column(nullable = false, precision = 19, scale = 0, updatable = false)
	private BigDecimal balanceAfter;

	@Column(length = 100, updatable = false)
	private String description;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/** 계좌 잔액이 이미 변경된 뒤에 호출해야 balanceAfter가 맞게 기록된다. */
	public static AccountTransaction record(Account account, TransactionType type, BigDecimal amount, String description) {
		AccountTransaction tx = new AccountTransaction();
		tx.account = account;
		tx.type = type;
		tx.amount = amount;
		tx.balanceAfter = account.getBalance();
		tx.description = description;
		return tx;
	}
}
