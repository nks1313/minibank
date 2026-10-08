package com.study.minibank.common.exception;

import java.util.List;

/**
 * 오류 응답 본문. 모든 API가 같은 형태로 오류를 돌려준다.
 */
public record ErrorResponse(String code, String message, List<FieldErrorDetail> fieldErrors) {

	public record FieldErrorDetail(String field, String reason) {
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage(), List.of());
	}

	public static ErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> fieldErrors) {
		return new ErrorResponse(errorCode.name(), errorCode.getMessage(), fieldErrors);
	}
}
