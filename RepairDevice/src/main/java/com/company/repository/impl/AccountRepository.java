package com.company.repository.impl;

import com.company.entities.Account;
import com.company.repository.IAccountRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 3:36 PM 4/28/2021
 */
@Repository
@Transactional
public class AccountRepository implements IAccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Account account) {
        entityManager.persist(account);
    }

    @Override
    public void update(Account account) {
        entityManager.merge(account);
    }

    @Override
    public void delete(Account account) {
        entityManager.remove(account);
    }

    @Override
    public Account findByCode(String code) {
        Query query = entityManager.createQuery("SELECT a FROM Account a WHERE a.code =:code");
        query.setParameter("code", code);
        List<Account> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public List<Account> findAll() {
        Query query = entityManager.createQuery("SELECT a from Account a");
        List<Account> lst = query.getResultList();
        return lst;
    }

    @Override
    public Account findByUsername(String username) {
        Query query = entityManager.createQuery("SELECT a FROM Account a WHERE a.username=:username");
        query.setParameter("username", username);
        List<Account> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public Account findById(int id) {
        Query query = entityManager.createQuery("select a from Account a where a.id=:id");
        query.setParameter("id", id);
        List<Account> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public Account findByDepartmentId(int id) {
        Query query = entityManager.createQuery("SELECT a FROM Account a WHERE a.department.id =:id and a.roles = 'FIXER'");
        query.setParameter("id", id);
        List<Account> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public Account findAccountTCHC() {
        Query query = entityManager.createQuery("SELECT a FROM Account a WHERE a.roles = 'TCHC'");
        List<Account> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }
}
