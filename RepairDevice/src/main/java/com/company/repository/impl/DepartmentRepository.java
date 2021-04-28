package com.company.repository.impl;

import com.company.entities.Department;
import com.company.repository.IDepartmentRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 3:06 PM 4/28/2021
 */
@Repository
@Transactional
public class DepartmentRepository implements IDepartmentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Department department) {
        entityManager.persist(department);
    }

    @Override
    public void update(Department department) {
        entityManager.merge(department);
    }

    @Override
    public void delete(Department department) {
        entityManager.remove(department);
    }

    @Override
    public Department findByCode(String code) {
        Query query = entityManager.createQuery("select d from Department d where d.code=:code");
        query.setParameter("code", code);
        List<Department> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }

    @Override
    public List<Department> findAll() {
        Query query = entityManager.createQuery("select d from Department d");
        List<Department> lst = query.getResultList();
        return lst;
    }

    @Override
    public Department findByName(String name) {
        Query query = entityManager.createQuery("select d from Department d where d.name=:name");
        query.setParameter("name", name);
        List<Department> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.get(0);
    }
}
