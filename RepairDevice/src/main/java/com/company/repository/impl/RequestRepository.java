package com.company.repository.impl;

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
    public List<Request> findAllRequestNotAcceptOrWaiting(int department_id, String code, String status) {
        Query query;
        if (department_id != -1) {
            query = entityManager.createQuery("select request from  Request request,Account account where " +
                    "request.status =:status and account.code = request.createdBy and account.department.id =:department_id");

            query.setParameter("status", status);
            query.setParameter("department_id", department_id);
        } else if (!code.equals("")) {
            query = entityManager.createQuery("select request from  Request request where request.createdBy =:code and request.status =:status");
            query.setParameter("status", status);
            query.setParameter("code", code);
        } else {
            query = entityManager.createQuery("select r from Request r where  r.status=:status");
            query.setParameter("status", status);
        }

        List<Request> lst = query.getResultList();
        return lst;
    }

    @Override
    public List<Request> findAllRequestFinished() {
        Query query = entityManager.createQuery("select r from Request r where  r.status= 'FINISHED'");
        List<Request> lst = query.getResultList();
        return lst;
    }

    @Override
    public List<Request> findAllRequestFixing(String code) {
        Query query = entityManager.createQuery("select r from Request r where  r.status= 'FIXING' and r.assign =:code");
        query.setParameter("code", code);
        List<Request> lst = query.getResultList();
        return lst;
    }

    @Override
    public List<Request> findAllRequestFaild(String code) {
        Query query = entityManager.createQuery("select r from Request r where  r.status= 'FAILED' and r.department =:code");
        query.setParameter("code", code);
        List<Request> lst = query.getResultList();
        return lst;
    }
}
