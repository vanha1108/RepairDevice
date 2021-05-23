package com.company.repository.impl;

import com.company.entities.Department;
import com.company.entities.Request;
import com.company.repository.IRequestRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class RequestRepository implements IRequestRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveRequest(Request request) {
        entityManager.persist(request);
    }

    @Override
    public void updateRequest(Request request) {
        entityManager.merge(request);
    }

    @Override
    public void deleteRequest(Request request) {
        entityManager.remove(request);
    }

    @Override
    public Request findRequestByCode(String code) {
        Query query = entityManager.createQuery("select r from Request r where r.code=:code");
        query.setParameter("code", code);
        List<Request> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public List<Request> findAllRequestNotAcceptOrWaiting(String code, String status) {
        System.out.println(code);
        System.out.println(status);
        Query query = entityManager.createQuery("select r from Request r where r.createdBy=:code or r.modifiedBy =:code and r.status=:status");
        query.setParameter("code", code);
        query.setParameter("status",status);
        List<Request> lst = query.getResultList();
        return lst;
    }
}
