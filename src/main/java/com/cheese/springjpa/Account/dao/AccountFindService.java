package com.cheese.springjpa.Account.dao;

import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountFindService {

    private final AccountRepository accountRepository;

    public Account findById(long id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account findByEmail(final Email email) {
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new AccountNotFoundException(email));
    }

    public boolean isExistedEmail(Email email) {
        return accountRepository.existsByEmail(email);
    }
}
