package com.company.service;

import com.company.entities.Account;
import com.company.entities.Department;

import java.io.Serializable;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 7:17 PM 4/28/2021
 */
public interface IDepartmentService extends Serializable {
    Department save(Department department);

    Department update(Department department);

    boolean delete(String code);

    Department findByCode(String code);

    List<Department> findAll();

    Department findByName(String name);
}
