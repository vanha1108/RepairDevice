package com.company.controller;

import com.company.entities.Account;
import com.company.entities.Department;
import com.company.service.IAccountService;
import com.company.service.IDepartmentService;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Nguyễn Văn Hà
 * 7:21 PM 4/28/2021
 */
@RestController
public class DepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private IAccountService accountService;

    @PostMapping("/department")
    public ResponseEntity<?> insertDepartment(@RequestBody Department department) {
        if (GenericValidator.isBlankOrNull(department.getCode())) {
            return new ResponseEntity<>("Code cannot be null!", HttpStatus.NO_CONTENT);
        }
        Department dep = departmentService.findByCode(department.getCode());
        if(dep != null) {
            return new ResponseEntity<>("Code already exist!", HttpStatus.CONFLICT);
        }
        Department depName = departmentService.findByName(department.getName());
        if (depName != null){
            return new ResponseEntity<>("Name already exist!", HttpStatus.CONFLICT);
        }
        departmentService.save(department);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @PutMapping("/department/{code}")
    public ResponseEntity<?> updateDepartment(@RequestBody Department department, @PathVariable("code") String code) {
        Department dep = departmentService.findByCode(code);
        if (dep == null) {
            return new ResponseEntity("Department not found!", HttpStatus.NOT_FOUND);
        }
        Department depCheck = departmentService.findByName(department.getName());
        if(depCheck != null && !depCheck.getCode().equals(code)) {
            return new ResponseEntity<>("Name already exist!", HttpStatus.CONFLICT);
        }
        department.setId(dep.getId());
        department.setCode(code);
        departmentService.update(department);
        return new ResponseEntity(department, HttpStatus.OK);
    }

    @DeleteMapping("/department/{code}")
    public ResponseEntity<?> deleteDepartment(@PathVariable("code") String code) {
        if(departmentService.delete(code)) {
            return new ResponseEntity<>(code, HttpStatus.OK);
        }
        return new ResponseEntity<>("Department not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/department/{code}")
    public ResponseEntity<?> getDepartmentByCode(@PathVariable("code") String code) {
        Department department = departmentService.findByCode(code);
        if (department != null) {
            return new ResponseEntity<>(department, HttpStatus.OK);
        }
        return new ResponseEntity<>("Department not found!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/department")
    public ResponseEntity<?> getAllDepartment() {
        List<Department> departments = departmentService.findAll();
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

}
