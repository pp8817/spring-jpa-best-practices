package com.cheese.springjpa.Account.application;

import com.cheese.springjpa.Account.dao.AccountRepository;
import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import com.cheese.springjpa.Account.dto.AccountDto;
import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findById(long id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new AccountNotFoundException(id));
    }

    public Account findByEmail(final Email email) {
        return accountRepository.findByEmail(email).orElseThrow(
                () -> new AccountNotFoundException(email));
    }

//    @Transactional(readOnly = true)
//    public Page<Account> findAll(Pageable pageable) {
//        return accountRepository.findAll(pageable);
//    }

    @Transactional
    public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
        final Account account = findById(id);
        account.updateMyAccount(dto);
        return account;
    }

    public Account create(AccountDto.SignUpReq dto) {
        if (isExistedEmail(dto.getEmail())) // email에 일치하는 계정이 있다면
            throw new EmailDuplicationException(dto.getEmail()); // 중복 예외 발생
        return accountRepository.save(dto.toEntity()); //없다면 계정 생성
    }

    public boolean isExistedEmail(Email email) {
        return accountRepository.findByEmail(email).isPresent();
    }
}
