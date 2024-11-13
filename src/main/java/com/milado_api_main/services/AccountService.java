package com.milado_api_main.services;

import com.milado_api_main.dto.AccountDto;
import com.milado_api_main.entities.Account;
import com.milado_api_main.exception.AccountAlreadyExistsException;
import com.milado_api_main.mappers.AccountMapper;
import com.milado_api_main.repositries.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker compromisedPasswordChecker;


    @Transactional
    public AccountDto create(@NonNull final AccountDto accountCreationRequest) {
        final var emailId = accountCreationRequest.getEmail();
        final var AccountExistsWithEmailId = accountRepository.existsByEmail(emailId);
        if (Boolean.TRUE.equals(AccountExistsWithEmailId)) {
            throw new AccountAlreadyExistsException("Account with provided email-id already exists");
        }

        final var plainTextPassword = accountCreationRequest.getPassword();
        final var isPasswordCompromised = compromisedPasswordChecker.check(plainTextPassword).isCompromised();
        if (Boolean.TRUE.equals(isPasswordCompromised)) {
            throw new CompromisedPasswordException("The provided password is compromised and cannot be used for account creation.");
        }

        Account account = AccountMapper.INSTANCE.accountDtoToAccount(accountCreationRequest);
        account.setPassword(passwordEncoder.encode(plainTextPassword));
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.INSTANCE.accountToAccountDto(savedAccount);

    }

}
