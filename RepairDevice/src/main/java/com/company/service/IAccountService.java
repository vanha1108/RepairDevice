package com.company.service;

import com.company.entities.Account;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 4:22 PM 4/28/2021
 */
public interface IAccountService extends Serializable {

    Account save(Account account);

    Account update(Account account);

    boolean delete(String code);

    Account findByCode(String code);

    List<Account> findAll();

    Account findByUsername(String username);

    Account findById(int id);
}
