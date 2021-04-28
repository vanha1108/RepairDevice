package com.company.controller;

import com.company.entities.Account;
import com.company.repository.IAccountRepository;
import com.company.service.IAccountService;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Nguyễn Văn Hà
 * 3:38 PM 4/28/2021
 */
@RestController
public class AccountController {

    @Autowired
    private IAccountService accountService;

    @PostMapping(value = "/account")
    public ResponseEntity<?> insertAccount(@RequestBody Account account) {
        if(GenericValidator.isBlankOrNull(account.getCode())) {
            return new ResponseEntity("Code cannot be null!", HttpStatus.NO_CONTENT);
        }
        Account acc = accountService.findByCode(account.getCode());
        if (acc != null) {
            return new ResponseEntity("Code already exist!", HttpStatus.CONFLICT);
        }
        Account accCheck = accountService.findByUsername(account.getUsername());
        if (accCheck != null) {
            return new ResponseEntity("Username already exist!", HttpStatus.CONFLICT);
        }
        accountService.save(account);
        return new ResponseEntity(account, HttpStatus.OK);
    }

    @PutMapping( value = "/account/{code}")
    public ResponseEntity<?> updateAccount(@RequestBody Account account, @PathVariable("code") String code) {
        Account acc = accountService.findByCode(code);
        if (acc == null) {
            return new ResponseEntity("Account not found!", HttpStatus.NOT_FOUND);
        }
        Account accCheck = accountService.findByUsername(account.getUsername());
        if(accCheck != null && !accCheck.getCode().equals(code)) {
            return  new ResponseEntity<>("Username already exist!", HttpStatus.CONFLICT);
        }
        account.setId(acc.getId());
        account.setCode(code);
        accountService.update(account);
        return new ResponseEntity(account, HttpStatus.OK);
    }

    @DeleteMapping("/account/{code}")
    public ResponseEntity<?> delete(@PathVariable("code") String code) {
        if(accountService.delete(code)) {
            return new ResponseEntity<>(code, HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account/{code}")
    public ResponseEntity<?> getAccountByCode(@PathVariable("code") String code) {
        Account account = accountService.findByCode(code);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found!", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAllAccount() {
        List<Account> accounts = accountService.findAll();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}
