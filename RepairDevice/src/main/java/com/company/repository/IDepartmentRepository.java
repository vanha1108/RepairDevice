package com.company.repository;

import com.company.entities.Account;
import com.company.entities.Department;

import java.io.Serializable;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 4:08 PM 4/28/2021
 */
public interface IDepartmentRepository extends Serializable {
    void save(Department department);
    void update(Department department);
    void delete(Department department);
    Department findByCode(String code);
    List<Department> findAll();
    Department findByName(String name);

    List<Department> findDepartmentByType(Integer i);
}
