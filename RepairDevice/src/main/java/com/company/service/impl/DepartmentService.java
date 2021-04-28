package com.company.service.impl;

import com.company.entities.Account;
import com.company.entities.Department;
import com.company.repository.IDepartmentRepository;
import com.company.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 7:18 PM 4/28/2021
 */
@Service
@Transactional
public class DepartmentService implements IDepartmentService {

    @Autowired
    private IDepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        departmentRepository.save(department);
        return department;
    }

    @Override
    public Department update(Department department) {
        departmentRepository.update(department);
        return department;
    }

    @Override
    public boolean delete(String code) {
        Department department = findByCode(code);
        if (department != null) {
            departmentRepository.delete(department);
            return true;
        }
        return false;
    }

    @Override
    public Department findByCode(String code) {
        return departmentRepository.findByCode(code);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Department findByName(String name) {
        return departmentRepository.findByName(name);
    }
}
