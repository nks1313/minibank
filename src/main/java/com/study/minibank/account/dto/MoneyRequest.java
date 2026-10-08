package com.study.minibank.account.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 입금/출금 요청
 */
public record MoneyRequest(
		@NotNull(message = "금액은 필수입니다.")
		@Positive(message = "금액은 0보다 커야 합니다.")
		@Digits(integer = 15, fraction = 0, message = "금액은 15자리 이하의 정수(원 단위)여야 합니다.")
		BigDecimal amount,

		@Size(max = 100, message = "적요는 100자 이하여야 합니다.")
		String description
) {
}
