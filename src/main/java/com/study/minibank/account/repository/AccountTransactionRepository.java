package com.study.minibank.account.repository;

import com.study.minibank.account.domain.Account;
import com.study.minibank.account.domain.AccountTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

	/** SELECT ... FROM account_transaction WHERE account_id = ? ORDER BY id DESC */
	List<AccountTransaction> findAllByAccountOrderByIdDesc(Account account);
}
