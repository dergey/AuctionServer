package com.sergey.zhuravlev.auctionserver.core.service;

import com.sergey.zhuravlev.auctionserver.core.exception.BadRequestException;
import com.sergey.zhuravlev.auctionserver.core.exception.NotFoundException;
import com.sergey.zhuravlev.auctionserver.database.entity.Account;
import com.sergey.zhuravlev.auctionserver.database.entity.Image;
import com.sergey.zhuravlev.auctionserver.database.entity.LocalUser;
import com.sergey.zhuravlev.auctionserver.database.entity.User;
import com.sergey.zhuravlev.auctionserver.database.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserService userService;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Account getAccountByUser(User user) {
        return accountRepository
                .findAccountByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional(readOnly = true)
    public Account getAccountByUserEmail(String email) {
        return accountRepository
                .findAccountByUserPrincipalEmail(email)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional(readOnly = true)
    public Account getAccountByUsername(String username) {
        return accountRepository
                .findAccountByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional
    public Account createLocalAccount(String email, String password, String username, Image photo,
                                 String firstname, String lastname, String bio) {
        LocalUser localUser = userService.createLocalUser(email, password);
        return createUpdateAccount(localUser, username, photo, firstname, lastname, bio);
    }

    @Transactional
    public Account createUpdateAccount(User user, String username, Image photo, String firstname, String lastname, String bio) {
        Account account = accountRepository
                .findAccountByUser(user)
                .orElse(new Account());
        if (!username.equals(account.getUsername()) && accountRepository.existsByUsername(username)) {
            throw new BadRequestException("UsernameAlreadyRegistered");
        }
        account.setUser(user);
        account.setUsername(username);
        account.setPhoto(photo);
        account.setFirstname(firstname);
        account.setLastname(lastname);
        account.setBio(bio);
        account = accountRepository.save(account);
        return account;
    }
}
