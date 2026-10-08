package com.study.minibank.common.exception;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 컨트롤러에서 던져진 예외를 한곳에서 잡아 ErrorResponse로 변환한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.info("업무 오류: {}", errorCode);
		return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.of(errorCode));
	}

	/** @Valid 검증 실패 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
		List<ErrorResponse.FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
				.map(fe -> new ErrorResponse.FieldErrorDetail(fe.getField(), fe.getDefaultMessage()))
				.toList();
		return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.INVALID_INPUT, fieldErrors));
	}

	/** JSON 형식 오류 (예: 숫자 자리에 문자열) */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(ErrorCode.INVALID_INPUT));
	}

	/** 예상하지 못한 오류: 내부 정보는 로그에만 남기고 클라이언트에는 숨긴다 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
		// 없는 URL(404), 허용되지 않은 HTTP 메서드(405) 등 Spring이 상태코드를 정해둔 예외는 그대로 따른다
		if (e instanceof org.springframework.web.ErrorResponse springError) {
			return ResponseEntity.status(springError.getStatusCode())
					.body(new ErrorResponse(springError.getStatusCode().toString(), e.getMessage(), List.of()));
		}
		log.error("처리되지 않은 예외", e);
		return ResponseEntity.internalServerError().body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR));
	}
}
