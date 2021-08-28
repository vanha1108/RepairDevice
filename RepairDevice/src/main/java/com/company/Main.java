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
        if(departmentRepository.findAll().stream().count() <=1){
            Department department = new Department();
            department.setCode(UUID.randomUUID().toString());
            department.setName("Sửa chữa phần cứng");
            department.setType(-1);
            departmentRepository.save(department);

            Account manager = new Account();
            manager.setCode(UUID.randomUUID().toString());
            manager.setUsername("managerRoomFix1");
            manager.setPassword(passwordEncoder.encode("managerRoomFix1"));
            manager.setRoles(EnumRole.MANAGER.toString());
            manager.setDepartment(department);
            accountRepository.save(manager);

            Department department2 = new Department();
            department2.setCode(UUID.randomUUID().toString());
            department2.setName("Tư vấn, lắp đặt phần cứng");
            department2.setType(-1);
            departmentRepository.save(department2);

            Account manager2 = new Account();
            manager2.setCode(UUID.randomUUID().toString());
            manager2.setUsername("managerRoomFix2");
            manager2.setPassword(passwordEncoder.encode("managerRoomFix2"));
            manager2.setRoles(EnumRole.MANAGER.toString());
            manager2.setDepartment(department2);
            accountRepository.save(manager2);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        runData();
    }
}
