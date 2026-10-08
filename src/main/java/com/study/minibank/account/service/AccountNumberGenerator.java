package com.study.minibank.account.service;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/**
 * 계좌번호 생성기: "110" + 랜덤 9자리.
 * 별도 클래스(@Component)로 분리해두면 테스트에서 고정된 번호를 내도록 바꿔 끼우기 쉽다.
 */
@Component
public class AccountNumberGenerator {

	private static final String PREFIX = "110";

	public String generate() {
		long number = ThreadLocalRandom.current().nextLong(0, 1_000_000_000L);
		return PREFIX + String.format("%09d", number);
	}
}
