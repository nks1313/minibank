package com.study.minibank.common.exception;

import lombok.Getter;

/**
 * 업무 규칙 위반 시 던지는 예외.
 * RuntimeException(unchecked)이므로 @Transactional 메서드 밖으로 던져지면 트랜잭션이 롤백된다.
 */
@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
