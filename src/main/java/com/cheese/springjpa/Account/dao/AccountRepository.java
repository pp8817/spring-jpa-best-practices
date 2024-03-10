package com.cheese.springjpa.Account.dao;

import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountSupportRepository,
    QuerydslPredicateExecutor<Account> {

  Optional<Account> findByEmail(Email email);

  boolean existsByEmail(Email email);
}
