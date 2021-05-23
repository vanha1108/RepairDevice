package com.company.entities;

import com.company.constant.EnumRole;

import javax.persistence.*;
import java.util.List;

/**
 * Nguyễn Văn Hà
 * 2:40 PM 4/26/2021
 */
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String code;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String password;

    private String roles;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Account() {
    }

    public Account(String code, String username, String password, String roles, Department department) {
        this.code = code;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
