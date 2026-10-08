package com.study.minibank.account.dto;

import com.study.minibank.account.domain.AccountTransaction;
import com.study.minibank.account.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
		Long transactionId,
		TransactionType type,
		BigDecimal amount,
		BigDecimal balanceAfter,
		String description,
		LocalDateTime createdAt
) {
	public static TransactionResponse from(AccountTransaction tx) {
		return new TransactionResponse(
				tx.getId(),
				tx.getType(),
				tx.getAmount(),
				tx.getBalanceAfter(),
				tx.getDescription(),
				tx.getCreatedAt());
	}
}
