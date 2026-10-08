package com.study.minibank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 계좌 개설 요청. record = 생성자/getter/equals가 자동 생성되는 불변 데이터 클래스 (C의 struct와 비슷)
 */
public record OpenAccountRequest(
		@NotBlank(message = "예금주명은 필수입니다.")
		@Size(max = 50, message = "예금주명은 50자 이하여야 합니다.")
		String ownerName
) {
}
