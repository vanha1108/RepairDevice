package com.company.service.impl;

import com.company.configs.AccountUserDetail;
import com.company.entities.Account;
import com.company.repository.IAccountRepository;
import com.company.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 4:25 PM 4/28/2021
 */
@Service
@Transactional
public class AccountService implements IAccountService {

    @Autowired
    private IAccountRepository accountRepository;

    @Override
    public Account save(Account account) {
        accountRepository.save(account);
        return account;
    }

    @Override
    public Account update(Account account) {
        if (account != null) {
            accountRepository.update(account);
            return account;
        }
        return null;
    }

    @Override
    public boolean delete(String code) {
        Account account = findByCode(code);
        if (account != null) {
            accountRepository.delete(account);
            return true;
        }
        return false;
    }

    @Override
    public Account findByCode(String code) {
        return accountRepository.findByCode(code);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account findById(int id) {
        return accountRepository.findById(id);
    }
}
