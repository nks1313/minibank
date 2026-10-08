package com.study.minibank.account.repository;

import com.study.minibank.account.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 인터페이스만 선언하면 Spring Data JPA가 구현체를 자동으로 만들어준다.
 * 메서드 이름 규칙(findBy..., existsBy...)으로 SQL이 생성된다.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByAccountNumber(String accountNumber);

	boolean existsByAccountNumber(String accountNumber);
}
