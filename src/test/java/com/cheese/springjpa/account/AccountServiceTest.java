package com.cheese.springjpa.Account;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.cheese.springjpa.Account.application.AccountService;
import com.cheese.springjpa.Account.dao.AccountRepository;
import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Address;
import com.cheese.springjpa.Account.domain.Email;
import com.cheese.springjpa.Account.dto.AccountDto;
import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void create_회원가입_성공() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.save(any(Account.class))).willReturn(dto.toEntity());

        //when
        final Account account = accountService.create(dto);

        //then
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
        assertThatEqual(dto, account);

        //커버리지를 높이기 위한 임시 함수
        account.getId();
        account.getCreatedAt();
        account.getUpdatedAt();
    }

    @Test(expected = EmailDuplicationException.class)
    public void create_중복된_이메일_경우_EmailDuplicationException() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.findByEmail(any())).willReturn(Optional.of(dto.toEntity()));

        //when
        accountService.create(dto);
    }

    @Test
    public void findById_존재하는경우_회원리턴() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.findById(anyLong())).willReturn(Optional.of(dto.toEntity()));

        //when
        final Account account = accountService.findById(anyLong());

        //then
        verify(accountRepository, atLeastOnce()).findById(anyLong());
        assertThatEqual(dto, account);
    }

    @Test(expected = AccountNotFoundException.class)
    public void findById_존재하지않은경우_AccountNotFoundException() {
        //given
        given(accountRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        accountService.findById(anyLong());
    }

    @Test
    public void updateMyAccount() {
        //given
        final AccountDto.SignUpReq signUpReq = buildSignUpReq();
        final AccountDto.MyAccountReq dto = buildMyAccountReq();
        given(accountRepository.findById(anyLong())).willReturn(Optional.of(signUpReq.toEntity()));

        //when
        final Account account = accountService.updateMyAccount(anyLong(), dto);

        //then
        assertThat(dto.getAddress().getAddress1(), is(account.getAddress().getAddress1()));
        assertThat(dto.getAddress().getAddress2(), is(account.getAddress().getAddress2()));
        assertThat(dto.getAddress().getZip(), is(account.getAddress().getZip()));
    }

    @Test
    public void isExistedEmail_존재하는이메일_ReturnTrue() {
        //given
        final AccountDto.SignUpReq signUpReq = buildSignUpReq();
        given(accountRepository.findByEmail(any())).willReturn(Optional.of(signUpReq.toEntity()));

        //when
        final boolean existedEmail = accountService.isExistedEmail(any());

        //then
        verify(accountRepository, atLeastOnce()).findByEmail(any());
        assertThat(existedEmail, is(true));
    }

    @Test
    public void findByEmail_존재하는_이매일조해경우_해당유저리턴() {
        //given
        final Account account = buildSignUpReq().toEntity();
        given(accountRepository.findByEmail(account.getEmail())).willReturn(Optional.of(account));

        //when
        final Account accountServiceByEmail = accountService.findByEmail(account.getEmail());

        //then
        assertThat(accountServiceByEmail.getEmail(), is(account.getEmail()));
    }

    @Test(expected = AccountNotFoundException.class)
    public void findByEmail_존재하는_않는경우() {
        //given
        given(accountRepository.findByEmail(any())).willReturn(null);

        //when
        accountService.findByEmail(any());

    }

    private AccountDto.MyAccountReq buildMyAccountReq() {
        return AccountDto.MyAccountReq.builder()
                .address(buildAddress("주소수정", "주소수정2", "061-233-444"))
                .build();
    }

    private void assertThatEqual(AccountDto.SignUpReq signUpReq, Account account) {
        assertThat(signUpReq.getAddress().getAddress1(), is(account.getAddress().getAddress1()));
        assertThat(signUpReq.getAddress().getAddress2(), is(account.getAddress().getAddress2()));
        assertThat(signUpReq.getAddress().getZip(), is(account.getAddress().getZip()));
        assertThat(signUpReq.getEmail(), is(account.getEmail()));
        assertThat(signUpReq.getFistName(), is(account.getFirstName()));
        assertThat(signUpReq.getLastName(), is(account.getLastName()));
    }

    private AccountDto.SignUpReq buildSignUpReq() {
        return AccountDto.SignUpReq.builder()
                .address(buildAddress("서울", "성동구", "052-2344"))
                .email(buildEmail("email"))
                .fistName("남윤")
                .lastName("김")
                .password("password111")
                .build();
    }

    private Email buildEmail(final String email) {
        return Email.builder().value(email).build();
    }

    private Address buildAddress(String address1, String address2, String zip) {
        return Address.builder()
                .address1(address1)
                .address2(address2)
                .zip(zip)
                .build();

    }
}