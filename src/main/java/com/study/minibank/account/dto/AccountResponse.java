package com.study.minibank.account.dto;

import com.study.minibank.account.domain.Account;
import com.study.minibank.account.domain.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 계좌 응답. 엔티티를 그대로 반환하지 않고 DTO로 변환한다.
 * (내부 id 같은 필드 노출 방지 + 엔티티를 바꿔도 API 스펙이 깨지지 않도록)
 */
public record AccountResponse(
		String accountNumber,
		String ownerName,
		BigDecimal balance,
		AccountStatus status,
		LocalDateTime createdAt
) {
	public static AccountResponse from(Account account) {
		return new AccountResponse(
				account.getAccountNumber(),
				account.getOwnerName(),
				account.getBalance(),
				account.getStatus(),
				account.getCreatedAt());
	}
}
