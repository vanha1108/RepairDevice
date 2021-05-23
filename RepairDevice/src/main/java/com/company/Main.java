package com.company;

import com.company.constant.EnumRole;
import com.company.entities.Account;
import com.company.entities.Department;
import com.company.repository.IAccountRepository;
import com.company.repository.IDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

/**
 * Nguyễn Văn Hà
 * 3:03 PM 4/28/2021
 */
@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private IDepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String []args) {
        SpringApplication.run(Main.class, args);
    }

    private void runData(){
        if(accountRepository.findAll().stream().count() ==0){
            Department department = new Department();
            department.setCode(UUID.randomUUID().toString());
            department.setName("Phòng server");
            departmentRepository.save(department);

            Account account = new Account();
            account.setCode(UUID.randomUUID().toString());
            account.setUsername("employee");
            account.setPassword(passwordEncoder.encode("employee"));
            account.setRoles(EnumRole.EMPLOYEE.toString());
            account.setDepartment(department);
            accountRepository.save(account);

            Account manager = new Account();
            manager.setCode(UUID.randomUUID().toString());
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager"));
            manager.setRoles(EnumRole.MANAGER.toString());
            manager.setDepartment(department);
            accountRepository.save(manager);

            Account director = new Account();
            director.setCode(UUID.randomUUID().toString());
            director.setUsername("director");
            director.setPassword(passwordEncoder.encode("director"));
            director.setRoles(EnumRole.DIRECTOR.toString());
            director.setDepartment(department);
            accountRepository.save(director);

            Account tchc = new Account();
            tchc.setCode(UUID.randomUUID().toString());
            tchc.setUsername("tchc");
            tchc.setPassword(passwordEncoder.encode("tchc"));
            tchc.setRoles(EnumRole.TCHC.toString());
            tchc.setDepartment(department);
            accountRepository.save(tchc);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        runData();
    }
}
