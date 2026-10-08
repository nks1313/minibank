package com.study.minibank.account.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.study.minibank.account.service.AccountService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * API 테스트: 실제 서버를 띄우지 않고 MockMvc로 HTTP 요청/응답을 흉내 내서
 * URL 매핑, 입력값 검증, JSON 형식, 상태코드를 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	AccountService accountService;

	@Test
	@DisplayName("계좌 개설 시 201 Created와 계좌 정보를 반환한다")
	void open() throws Exception {
		mockMvc.perform(post("/api/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"ownerName": "홍길동"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", startsWith("/api/accounts/110")))
				.andExpect(jsonPath("$.ownerName").value("홍길동"))
				.andExpect(jsonPath("$.balance").value(0))
				.andExpect(jsonPath("$.status").value("ACTIVE"));
	}

	@Test
	@DisplayName("예금주명이 비어 있으면 400과 필드 오류를 반환한다")
	void openInvalid() throws Exception {
		mockMvc.perform(post("/api/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"ownerName": " "}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_INPUT"))
				.andExpect(jsonPath("$.fieldErrors[0].field").value("ownerName"));
	}

	@Test
	@DisplayName("음수 금액으로 입금하면 400을 반환한다")
	void depositNegativeAmount() throws Exception {
		String accountNumber = accountService.open("홍길동").accountNumber();

		mockMvc.perform(post("/api/accounts/{accountNumber}/deposits", accountNumber)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"amount": -1000}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors[0].field").value("amount"));
	}

	@Test
	@DisplayName("잔액보다 많이 출금하면 422와 INSUFFICIENT_BALANCE를 반환한다")
	void withdrawInsufficientBalance() throws Exception {
		String accountNumber = accountService.open("홍길동").accountNumber();
		accountService.deposit(accountNumber, new BigDecimal("1000"), null);

		mockMvc.perform(post("/api/accounts/{accountNumber}/withdrawals", accountNumber)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"amount": 5000}
								"""))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.code").value("INSUFFICIENT_BALANCE"));
	}

	@Test
	@DisplayName("없는 계좌를 조회하면 404를 반환한다")
	void accountNotFound() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountNumber}", "999999999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
	}
}
