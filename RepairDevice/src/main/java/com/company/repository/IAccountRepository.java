package com.company.repository;

import com.company.entities.Account;

import java.io.Serializable;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 3:36 PM 4/28/2021
 */
public interface IAccountRepository extends Serializable {

    void save(Account account);
    void update(Account account);
    void delete(Account account);
    Account findByCode(String code);
    List<Account> findAll();
    Account findByUsername(String username);
    Account findById(int id);
}
