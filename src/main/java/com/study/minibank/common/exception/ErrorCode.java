package com.study.minibank.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 업무 오류 코드 목록. (C의 오류코드 #define 테이블과 같은 역할)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "거래 금액은 0보다 커야 합니다."),
	ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌를 찾을 수 없습니다."),
	ACCOUNT_NOT_ACTIVE(HttpStatus.UNPROCESSABLE_CONTENT, "거래할 수 없는 상태의 계좌입니다."),
	INSUFFICIENT_BALANCE(HttpStatus.UNPROCESSABLE_CONTENT, "잔액이 부족합니다."),
	ACCOUNT_NUMBER_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "계좌번호 생성에 실패했습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String message;
}
